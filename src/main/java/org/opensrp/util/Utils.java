package org.opensrp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.repository.postgres.handler.BaseTypeHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utils {
	
	private Utils() {
		
	}
	
	public static Map<String, String> getStringMapFromJSON(String fields) {
		return new Gson().fromJson(fields, new TypeToken<Map<String, String>>() {}.getType());
	}
	
	public static List<String> getFieldsAsList(Class<?> cls) {
		List<String> fieldList = new ArrayList<>();
		Field[] fieldSet = cls.getDeclaredFields();
		for (Field field : fieldSet) {
			if (!field.isSynthetic()) {
				fieldList.add(field.getName());
			}
		}
		return fieldList;
	}
	
	public static Object getMergedJSON(Object original, Object updated, List<Field> fn, Class<?> clazz)
	        throws JSONException, JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper= BaseTypeHandler.createObjectMapper();
		JSONObject originalJo = new JSONObject(objectMapper.writeValueAsString(original));
		
		JSONObject updatedJo = new JSONObject(objectMapper.writeValueAsString(updated));
		
		JSONObject mergedJson = new JSONObject();
		if (originalJo.length() > 0) {
			mergedJson = new JSONObject(originalJo, JSONObject.getNames(originalJo));
		}
		
		if (updatedJo.length() > 0) {
			for (Field key : fn) {
				String jokey = key.getName();
				if (updatedJo.has(jokey))
					mergedJson.put(jokey, updatedJo.get(jokey));
			}
		}
		if (mergedJson.length() > 0)
			return objectMapper.readValue(mergedJson.toString(), clazz);
		return original;
	}
	
	public static JSONArray getXlsToJson(String path) throws JSONException, IOException {
		FileInputStream inp = new FileInputStream(new File(path));
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(inp);
		
		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		int hrn = getHeaderRowNum(sheet);
		List<String> hr = getRowContent(sheet, hrn);
		// Start constructing JSON.
		JSONArray jarr = new JSONArray();
		
		for (int i = hrn + 1; i <= sheet.getLastRowNum(); i++) {
			List<String> rc = getRowContent(sheet, i);
			if (!isRowEmpty(rc)) {
				JSONObject row = new JSONObject();
				for (int j = 0; j < hr.size(); j++) {
					row.put(hr.get(j), rc.get(j));
				}
				jarr.put(row);
			}
		}
		
		workbook.close();
		return jarr;
	}
	
	private static int getHeaderRowNum(HSSFSheet sheet) {
		Iterator<Row> i = sheet.iterator();
		while (i.hasNext()) {
			Row r = i.next();
			for (Cell c : r) {
				if (!StringUtils.isBlank(c.getStringCellValue())) {
					return r.getRowNum();
				}
			}
		}
		return -1;
	}
	
	private static boolean isRowEmpty(List<String> rcontent) {
		for (String r : rcontent) {
			if (!StringUtils.isBlank(r)) {
				return false;
			}
		}
		return true;
	}
	
	private static List<String> getRowContent(HSSFSheet sheet, int rowNum) {
		List<String> hc = new ArrayList<>();
		HSSFRow r = sheet.getRow(rowNum);
		if (r != null && r.getPhysicalNumberOfCells() > 0) {
			for (int i = 0; i < r.getLastCellNum(); i++) {
				Cell c = r.getCell(i);
				hc.add(c == null ? "" : c.getStringCellValue());
			}
		}
		
		/*
		 * Iterator<Cell> it = sheet.getRow(rowNum).cellIterator(); while
		 * (it.hasNext()) { Cell c = it.next(); hc.add(c.getStringCellValue());
		 * }
		 */
		return hc;
	}
	
	/**
	 * Connect to the database specified by DatabaseConnectionParams
	 * 
	 * @param dbParams
	 * @return
	 * @throws MalformedURLException
	 */
	public static CouchDbConnector connectToDB(DatabaseConnectionParams dbParams) throws MalformedURLException {
		HttpClient authenticatedHttpClient = null;
		
		if (dbParams.userName != null && !dbParams.userName.isEmpty() && dbParams.password != null
		        && !dbParams.password.isEmpty()) {
			
			authenticatedHttpClient = new StdHttpClient.Builder().url(dbParams.url.concat(":").concat(dbParams.portNumber))
			        .username(dbParams.userName).password(dbParams.password).build();
		} else {
			authenticatedHttpClient = new StdHttpClient.Builder().url(dbParams.url.concat(":").concat(dbParams.portNumber))
			        .build();
		}
		
		CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
		
		CouchDbConnector db = new StdCouchDbConnector(dbParams.dbName, dbInstance);
		return db;
		
	}
	
	public static CouchDbInstance getDbInstance(DatabaseConnectionParams dbParams) throws MalformedURLException {
		HttpClient authenticatedHttpClient = null;
		
		if (dbParams.userName != null && !dbParams.userName.isEmpty() && dbParams.password != null
		        && !dbParams.password.isEmpty()) {
			
			authenticatedHttpClient = new StdHttpClient.Builder().url(dbParams.url.concat(":").concat(dbParams.portNumber))
			        .username(dbParams.userName).password(dbParams.password).build();
		} else {
			authenticatedHttpClient = new StdHttpClient.Builder().url(dbParams.url.concat(":").concat(dbParams.portNumber))
			        .build();
		}
		
		CouchDbInstance dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
		return dbInstance;
	}

	public static boolean isEmptyList(List list) {
		return list == null || list.size() == 0;
	}

	public static class DatabaseConnectionParams {
		
		public String url;
		
		public String portNumber;
		
		public String userName;
		
		public String password;
		
		public String dbName;
	}
}
