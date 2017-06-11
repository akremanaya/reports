package com.innvo.jasper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innvo.domain.Report;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
/**
 * 
 * @author ali
 *
 */
@Component
public class GenerateReportFile {
	
	ParsingService parsingService=new ParsingService();   
	
	@Autowired
	JasperConfiguration jasperConfiguration;
	
	
	public byte[] generateReport(Report report,String parameters) throws Exception{
		byte[] output=null;
			output=generateReportEngine(report, parameters);		
	    return output;
}	


 public byte[] generateReportEngine(Report report,String params) throws JRException, JsonParseException, JsonMappingException, IOException{
	
	DBConnection connection=new DBConnection();
   	
	JasperReport jasperReport = JasperCompileManager
               .compileReport(jasperConfiguration.getJrxmlpath()+report.getReporttemplatename()+".jrxml");
       ObjectMapper mapper = new ObjectMapper();
	       List<Parameters> objects = mapper.readValue(params, new TypeReference<List<Parameters>>(){});
       Map<String,Object> parametersMap=new HashMap<String,Object>();
       for(Parameters param:objects){
    	   parametersMap.put(param.getKey(), param.getValue());
       }
	    
       JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
               parametersMap, connection.getConnection());
       JasperExportManager.exportReportToPdfFile(jasperPrint,
               jasperConfiguration.getReportpath()+report.getReporttemplatename()+"."+report.getReportoutputtypecode());
       
       byte[] output = JasperExportManager.exportReportToPdf(jasperPrint);
       System.out.println(output);
       return output; 

}

}
