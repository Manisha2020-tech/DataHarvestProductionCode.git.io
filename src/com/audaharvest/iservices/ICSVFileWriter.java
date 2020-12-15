package com.audaharvest.iservices;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

import com.audaharvest.model.Vehicle;

public interface ICSVFileWriter {
	public void writeCSVFile(String fileName, String garbageFileName, Set<Future<Vehicle>> set, Map<String, String> urlMap);
	public void writeCSVFileInventoryPage(String fileName, String garbageFileName, Set<Vehicle> set, Map<String, String> urlMap);
}
