package org.univaq.swa.framework.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author miche    
 */
public class CsvUtility {
    private static final String PATH_CONFIGURAZIONE_AULE = "csv\\configurazione_aule.csv";
    
    public static void csvAuleWrite(File fileCsv, ArrayList<HashMap<String, Object>> configurazioneAule){
        try{
            FileWriter fileWriter = new FileWriter(fileCsv);
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader("ID", "NOME AULA", "LUOGO","EDIFICIO","PIANO","CAPIENZA", "EMAIL RESPONSABILE","PRESE ELETTRICHE","PRESE RETE","NOTE","NOME GRUPPO")
                    .build();
            try(CSVPrinter csvPrinter = new CSVPrinter(fileWriter, format)){
                for(HashMap<String, Object> aula: configurazioneAule){
                    var id = aula.get("id");
                    var nomeAula = aula.get("nomeAula");
                    var luogo = aula.get("luogo");
                    var edificio = aula.get("edificio");
                    var piano = aula.get("piano");
                    var capienza = aula.get("capienza");
                    var emailResponsabile = aula.get("emailResponsabile");
                    var preseElettriche = aula.get("preseElettriche");
                    var preseRete = aula.get("preseRete");
                    var note = aula.get("note");
                    var nomeGruppo = aula.get("nomeGruppo")!=null ? aula.get("nomeGruppo") : "";
                    csvPrinter.printRecord(id, nomeAula, luogo, edificio, piano, capienza, emailResponsabile, preseElettriche, preseRete, note, nomeGruppo);
                }
                csvPrinter.flush();
                csvPrinter.close();
                fileWriter.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(CsvUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static ArrayList<HashMap<String, Object>> csvAuleRead(InputStream fileCsv){
        System.out.println("---------------> MI TROVO IN CSV AULE READ");
        ArrayList<HashMap<String, Object>> configurazioneAule = new ArrayList<HashMap<String,Object>>();
        
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileCsv))){
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader("NOME AULA", "LUOGO","EDIFICIO","PIANO","CAPIENZA", "EMAIL RESPONSABILE","PRESE ELETTRICHE","PRESE RETE","NOTE","NOME GRUPPO")
                    .setSkipHeaderRecord(true)
                    .build();
            Iterable<CSVRecord> records = format.parse(fileReader);
            
            //remove the last useless row
            records.iterator().next();
            records.iterator().next();
            records.iterator().next();
            for (CSVRecord csvRecord : records) {
                // the last row is useless, so when i'm in the last rown (it hasn't a next element) i break the foreach cycle
                if (!records.iterator().hasNext()){
                    break;
                }
            System.out.println("----------->: " + csvRecord);    
            //creo una nuova aula sotto forma di hash map            
            HashMap<String, Object> aula = new HashMap<String, Object>();

            aula.put("nomeAula", csvRecord.get("NOME AULA"));
            aula.put("luogo", csvRecord.get("LUOGO"));
            aula.put("edificio", csvRecord.get("EDIFICIO"));
            aula.put("piano", csvRecord.get("PIANO"));
            aula.put("capienza", csvRecord.get("CAPIENZA"));
            aula.put("emailResponsabile", csvRecord.get("EMAIL RESPONSABILE"));
            aula.put("preseElettriche", csvRecord.get("PRESE ELETTRICHE"));
            aula.put("preseRete", csvRecord.get("PRESE RETE"));
            aula.put("note", csvRecord.get("NOTE"));
            aula.put("nomeGruppo", csvRecord.get("NOME GRUPPO"));
            
            configurazioneAule.add(aula);
        }
        fileReader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return configurazioneAule;
    }
}
