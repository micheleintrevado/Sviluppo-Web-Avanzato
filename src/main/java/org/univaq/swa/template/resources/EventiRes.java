package org.univaq.swa.template.resources;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.validate.ValidationException;
import org.univaq.swa.framework.model.Attrezzatura;
import org.univaq.swa.framework.model.Evento;
import org.univaq.swa.framework.model.Tipologia;
import org.univaq.swa.framework.security.Logged;
import org.univaq.swa.template.exceptions.RESTWebApplicationException;

/**
 *
 * @author miche
 */
@Path("eventi")
public class EventiRes {

    @Context
    private ServletContext servletContext;

    private static final String DB_NAME = "java:comp/env/jdbc/auleweb";
    private static final String QUERY_SELECT_EVENTI_ATTUALI = "select * from evento where orario_inizio < now() and orario_fine > now();";
    private static final String QUERY_SELECT_EVENTI_PROSSIME_ORE = "select * from evento where orario_inizio < date_add(now(), INTERVAL ? hour) and orario_fine >= now()";
    private static final String QUERY_SELECT_EVENTI_RANGE = "SELECT * FROM auleweb.evento where orario_inizio > ? and orario_fine < ?;";
    private static final String QUERY_SELECT_EVENTI_ID_MASTER = "SELECT * FROM auleweb.evento where id_master = ?;";
    private static final String QUERY_SELECT_ID_CORSI = "SELECT id FROM corso;";
    private static final String QUERY_SELECT_ID_EVENTI = "SELECT id FROM evento;", QUERY_SELECT_ID_RICORRENZE = "SELECT id FROM RICORRENZA";

    private static Connection getPooledConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource ds = (DataSource) context.lookup(DB_NAME);
        return ds.getConnection();
    }

    public EventiRes() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private Evento createEvento(Map<String, Object> evento) {
        Evento e = new Evento();
        e.setId((int) evento.get("id"));
        e.setNome((String) evento.get("nome"));
        e.setOrarioInizio((LocalDateTime) evento.get("orarioInizio"));
        e.setOrarioFine((LocalDateTime) evento.get("orarioFine"));
        e.setDescrizione((String) evento.get("descrizione"));
        e.setNomeOrganizzatore((String) evento.get("nomeOrganizzatore"));
        e.setEmailResponsabile((String) evento.get("emailResponsabile"));
        e.setTipologia((Tipologia) evento.get("tipologia"));

        return e;

    }

    @GET
    @Path("ids")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getidEventi() throws RESTWebApplicationException {
        try {
            ArrayList<Integer> idEventi = new ArrayList<Integer>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_ID_EVENTI)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        idEventi.add(rs.getInt("id"));
                    }
                }
            }
            return Response.ok(idEventi).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }
    
    @GET
    @Path("ricorrenze")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getidRicorrenze() throws RESTWebApplicationException {
        try {
            ArrayList<Integer> idRicorrenze = new ArrayList<Integer>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_ID_RICORRENZE)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        idRicorrenze.add(rs.getInt("id"));
                    }
                }
            }
            return Response.ok(idRicorrenze).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // 12
    @GET
    @Produces("text/calendar")
    public Response getEventiForRange(@QueryParam("rangeStart") String rangeStart, @QueryParam("rangeEnd") String rangeEnd) throws FileNotFoundException {
        ArrayList<Evento> eventi = new ArrayList<Evento>();
        LocalDateTime dataOraInizio = LocalDateTime.parse(rangeStart, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime dataOraFine = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ISO_DATE_TIME);

        try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_RANGE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(dataOraInizio));
            ps.setTimestamp(2, Timestamp.valueOf(dataOraFine));
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evento e = obtainEvento(rs);
                    eventi.add(e);
                }
            }
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }

        Calendar calendar = new Calendar();
        calendar.add(new ProdId("AuleWebServices"));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);

        for (Evento e : eventi) {
            String summary = e.getNome() + ": " + e.getDescrizione();
            VEvent eventICal = new VEvent(e.getOrarioInizio(), e.getOrarioFine(), summary);
            calendar.add(eventICal);
        }

        String path = servletContext.getRealPath("");
        File file = new File(path, "ical\\eventi.ics");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fout = new FileOutputStream(file);
        CalendarOutputter outputter = new CalendarOutputter();
        try {
            outputter.output(calendar, fout);
        } catch (IOException | ValidationException ex) {
            Logger.getLogger(EventiRes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Response.ok(file, "text/calendar").header("Content-Disposition", "attachment;filename=calendar.ics").build();
    }

    @PATCH
    @Path("{id: [1-9][0-9]*}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Logged
    public Response updateEvento(@PathParam("id") int idEvento, Map<String, Object> fieldsToUpdate) {
        Evento evento = new Evento();
        evento.setId(idEvento);
        EventoRes eventoRes = new EventoRes(evento);
        return eventoRes.updateEvento(idEvento, fieldsToUpdate);
    }

    @GET
    @Path("{id: [1-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvento(@PathParam("id") int idEvento) throws RESTWebApplicationException {
        Evento evento = new Evento();
        evento.setId(idEvento);
        EventoRes eventoRes = new EventoRes(evento);
        return eventoRes.getInfoEvento(idEvento);
    }

    @GET
    @Path("idMaster/{id: [1-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiIdMaster(@PathParam("id") int id_master) {
        try {
            ArrayList<Evento> eventi = new ArrayList<Evento>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_ID_MASTER)) {
                ps.setInt(1, id_master);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Evento e = obtainEvento(rs);
                        eventi.add(e);
                    }
                }
            }
            return Response.ok(eventi).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // 7
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Logged
    public Response addEvento(@Context UriInfo uriinfo, HashMap<String, Object> evento) {
        String addEventoNonRicorrenteQuery = "INSERT INTO `evento` (`nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String addEventoRicorrenteQuery = "INSERT INTO `evento` (`nome`, `orario_inizio`, `orario_fine`, `descrizione`, `nome_organizzatore`, `email_responsabile`, `tipologia`, `id_aula`, `id_corso`, `id_master`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String addRicorrenzaQuery = "INSERT INTO `ricorrenza` (`tipo`, `data_termine`) VALUES ( ?, ?);";
        Integer id_master = null;

        // gestione dell'evento ricorrente
        if (evento.get("tipo") != null && evento.get("data_termine") != null) {
            // preparazione statement inserimento della ricorrenza (che sarà l'"id master" dell'evento)
            try ( Connection con = getPooledConnection();  PreparedStatement addRicorrenzaStatement = con.prepareStatement(addRicorrenzaQuery, Statement.RETURN_GENERATED_KEYS)) {
                addRicorrenzaStatement.setString(1, (String) evento.get("tipo"));

                var dataTermine = evento.get("data_termine");
                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                LocalDateTime dateTermineRicorrenza = LocalDateTime.parse((String) dataTermine, DateTimeFormatter.ISO_DATE_TIME);
                addRicorrenzaStatement.setTimestamp(2, Timestamp.valueOf(dateTermineRicorrenza));

                //creazione della ricorrenza nel DB e salvataggio del suo ID (che sarà l'id master dell'evento)
                addRicorrenzaStatement.executeUpdate();
                try ( ResultSet rsAddRicorrenza = addRicorrenzaStatement.getGeneratedKeys();) {
                    rsAddRicorrenza.next();
                    id_master = rsAddRicorrenza.getInt(1);
                }

                ArrayList<LocalDateTime> dateRicorrenzeInizio = new ArrayList<LocalDateTime>();
                ArrayList<LocalDateTime> dateRicorrenzeFine = new ArrayList<LocalDateTime>();

                var dataOrarioInizio = evento.get("orario_inizio");
                LocalDate dataInizioEvento = LocalDateTime.parse((String) dataOrarioInizio, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
                LocalTime orarioInizioEvento = LocalDateTime.parse((String) dataOrarioInizio, DateTimeFormatter.ISO_DATE_TIME).toLocalTime();

                var dataOrarioFine = evento.get("orario_fine");
                LocalDate dataFineEvento = LocalDateTime.parse((String) dataOrarioFine, DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
                LocalTime orarioFineEvento = LocalDateTime.parse((String) dataOrarioFine, DateTimeFormatter.ISO_DATE_TIME).toLocalTime();

                // popolo le liste dateRicorrenzeInizio e dateRicorrenzeFine che contengono le ricorrenze dell'evento fino alla scadenza della ricorrenza
                while (!dataFineEvento.isAfter(dateTermineRicorrenza.toLocalDate())) {
                    LocalDateTime dataOraInizioEvento = LocalDateTime.of(dataInizioEvento, orarioInizioEvento);
                    LocalDateTime dataOraFineEvento = LocalDateTime.of(dataFineEvento, orarioFineEvento);

                    dateRicorrenzeInizio.add(dataOraInizioEvento);
                    dateRicorrenzeFine.add(dataOraFineEvento);

                    switch (evento.get("tipo").toString()) {
                        case "giornaliera":
                            dataInizioEvento = dataInizioEvento.plusDays(1);
                            dataFineEvento = dataFineEvento.plusDays(1);
                            break;
                        case "settimanale":
                            dataInizioEvento = dataInizioEvento.plusWeeks(1);
                            dataFineEvento = dataFineEvento.plusWeeks(1);
                            break;
                        case "mensile":
                            dataInizioEvento = dataInizioEvento.plusMonths(1);
                            dataFineEvento = dataFineEvento.plusMonths(1);
                            break;
                    }
                }

                Iterator<LocalDateTime> ricorrenzeInizioIterator = dateRicorrenzeInizio.iterator();
                Iterator<LocalDateTime> ricorrenzeFineIterator = dateRicorrenzeFine.iterator();
                while (ricorrenzeInizioIterator.hasNext() && ricorrenzeFineIterator.hasNext()) {
                    LocalDateTime dataInizioRicorrenzaEvento = ricorrenzeInizioIterator.next();
                    LocalDateTime dataFineRicorrenzaEvento = ricorrenzeFineIterator.next();
                    try ( PreparedStatement ps = con.prepareStatement(addEventoRicorrenteQuery, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setString(1, (String) evento.get("nome"));
                        ps.setTimestamp(2, Timestamp.valueOf(dataInizioRicorrenzaEvento));
                        ps.setTimestamp(3, Timestamp.valueOf(dataFineRicorrenzaEvento));
                        ps.setString(4, (String) evento.get("descrizione"));
                        ps.setString(5, (String) evento.get("nome_organizzatore"));
                        ps.setString(6, (String) evento.get("email_responsabile"));
                        ps.setString(7, (String) evento.get("tipologia"));
                        ps.setInt(8, (int) evento.get("id_aula"));
                        if (evento.get("id_corso") != null) {
                            ps.setInt(9, (int) evento.get("id_corso"));
                        } else {
                            ps.setObject(9, null);
                        }
                        ps.setInt(10, id_master);

                        ps.executeUpdate();

                    } catch (SQLException ex) {
                        throw new RESTWebApplicationException(ex);
                    }
                }

                URI uri = uriinfo.getBaseUriBuilder()
                        .path(EventiRes.class)
                        .path(EventiRes.class, "getEventiIdMaster")
                        .build(id_master);
                return Response.created(uri).build();
            } catch (SQLException | NamingException ex) {
                throw new RESTWebApplicationException(ex);
            }
        } else {
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(addEventoNonRicorrenteQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, (String) evento.get("nome"));

                var orarioInizio = evento.get("orario_inizio");
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse((String) orarioInizio, DateTimeFormatter.ISO_DATE_TIME);
                ps.setTimestamp(2, Timestamp.valueOf(dateTime));

                var orarioFine = evento.get("orario_fine");
                dateTime = LocalDateTime.parse((String) orarioFine, DateTimeFormatter.ISO_DATE_TIME);
                ps.setTimestamp(3, Timestamp.valueOf(dateTime));
                ps.setString(4, (String) evento.get("descrizione"));
                ps.setString(5, (String) evento.get("nome_organizzatore"));
                ps.setString(6, (String) evento.get("email_responsabile"));
                ps.setString(7, (String) evento.get("tipologia"));
                ps.setInt(8, (int) evento.get("id_aula"));
                if (evento.get("id_corso") != null) {
                    ps.setInt(9, (int) evento.get("id_corso"));
                } else {
                    ps.setObject(9, null);
                }
                ps.executeUpdate();

                try ( ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    int idEvento = keys.getInt(1);
                    URI uri = uriinfo.getBaseUriBuilder()
                            .path(EventiRes.class)
                            .path(EventiRes.class, "getEvento")
                            .build(idEvento);
                    return Response.created(uri).build();
                }
            } catch (SQLException | NamingException ex) {
                throw new RESTWebApplicationException(ex);
            }
        }        // return Response.noContent().build();
    }

    // 11
    @GET
    @Path("attuali")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiAttuali() {
        try {
            ArrayList<Evento> eventiAttuali = new ArrayList<Evento>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_ATTUALI)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        eventiAttuali.add(obtainEvento(rs));
                    }
                }
            }

            return Response.ok(eventiAttuali).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    // 11
    @GET
    @Path("prossimi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventiProssimi(@QueryParam("prossimeOre") @DefaultValue("3") int prossimeOre) {
        try {
            ArrayList<Evento> eventiAttuali = new ArrayList<Evento>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_EVENTI_PROSSIME_ORE)) {
                ps.setInt(1, prossimeOre);
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        eventiAttuali.add(obtainEvento(rs));
                    }
                }
            }

            return Response.ok(eventiAttuali).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    @GET
    @Path("corsi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getidCorsi() throws RESTWebApplicationException {
        try {
            ArrayList<Integer> idCorsi = new ArrayList<Integer>();
            try ( Connection con = getPooledConnection();  PreparedStatement ps = con.prepareStatement(QUERY_SELECT_ID_CORSI)) {
                try ( ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        idCorsi.add(rs.getInt("id"));
                    }
                }
            }
            return Response.ok(idCorsi).build();
        } catch (SQLException | NamingException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

    private Evento obtainEvento(ResultSet rs) {
        try {
            Evento e = new Evento();

            e.setId(rs.getInt("id"));
            e.setNome(rs.getString("nome"));
            e.setOrarioInizio(rs.getTimestamp("orario_inizio").toLocalDateTime());
            e.setOrarioFine(rs.getTimestamp("orario_fine").toLocalDateTime());
            e.setDescrizione(rs.getString("descrizione"));
            e.setNomeOrganizzatore(rs.getString("nome_organizzatore"));
            e.setEmailResponsabile(rs.getString("email_responsabile"));
            e.setTipologia(Tipologia.valueOf(rs.getString("tipologia")));
            e.setIdAula(rs.getInt("id_aula"));
            e.setIdCorso(rs.getInt("id_corso"));
            return e;
        } catch (SQLException ex) {
            throw new RESTWebApplicationException(ex);
        }
    }

}
