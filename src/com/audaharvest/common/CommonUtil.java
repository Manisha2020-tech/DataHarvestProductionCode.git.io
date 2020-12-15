package com.audaharvest.common;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CommonUtil {
	@SuppressWarnings("unchecked")
	public Map<String, Integer> sortByValues(Map<String, Integer> map) { 
	       List<String> list = new LinkedList(map.entrySet());
	       // Defined Custom Comparator here
	       Collections.sort(list, new Comparator() {
	            public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	                  .compareTo(((Map.Entry) (o2)).getValue());
	            }
	       });

	       // Here I am copying the sorted list in HashMap
	       // using LinkedHashMap to preserve the insertion order
	       HashMap sortedHashMap = new LinkedHashMap();
	       for (Iterator it = list.iterator(); it.hasNext();) {
	              Map.Entry entry = (Map.Entry) it.next();
	              sortedHashMap.put(entry.getKey(), entry.getValue());
	       } 
	       return sortedHashMap;
	  }
	
	public int nextMaxInSortedArray(int x, Integer[] mapValues) {
		int temp = 0;
		for(int i=0; i<mapValues.length; i++) {
			if(x == Integer.valueOf(mapValues[i])) {
				temp = Integer.valueOf(mapValues[i+1]);
			}
		}
		
		return temp;
		
	}
	
	public boolean vinCheck(String s) {
		boolean flag = false;
		int[] values = { 1, 2, 3, 4, 5, 6, 7, 8, 0, 1,
				2, 3, 4, 5, 0, 7, 0, 9, 2, 3,
				4, 5, 6, 7, 8, 9 };
		int[] weights = { 8, 7, 6, 5, 4, 3, 2, 10, 0, 9,
				8, 7, 6, 5, 4, 3, 2 };


		s = s.replaceAll("-", "");
		s = s.toUpperCase();
		try{
			
		
		if (s.length() != 17)
			throw new RuntimeException("VIN number must be 17 characters");

		int sum = 0;
		for (int i = 0; i < 17; i++) {
			char c = s.charAt(i);
			int value;
			int weight = weights[i];

			// letter
			if (c >= 'A' && c <= 'Z') {
				value = values[c - 'A'];
				if (value == 0)
					throw new RuntimeException("Illegal character: " + c);
			}

			// number
			else if (c >= '0' && c <= '9') value = c - '0';

			// illegal character
			else throw new RuntimeException("Illegal character: " + c);

			sum = sum + weight * value;

		}

		// check digit
		sum = sum % 11;
		char check = s.charAt(8);
		if (check != 'X' && (check < '0' || check > '9'))
			throw new RuntimeException("Illegal check digit: " + check);
			if (sum == 10 && check == 'X') flag = true;
			else if (sum == check - '0')   flag = true;
			else                           flag = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	//Reverse the string e.g Hello World -> World Hello
	public String reverseString(String input) {
		StringBuilder sb = new StringBuilder(input);
		String reversedWords = sb.reverse().toString();
		String[] words = reversedWords.split(" ");
		String inputReversed = "";
		for(int i=0; i<words.length; i++) {
			StringBuilder sb2 = new StringBuilder(words[i]);
			String singleWordReversed = sb2.reverse().toString();
			inputReversed += singleWordReversed + " ";
		}
		return inputReversed.trim();
	}
	
	public void sslCert() {
		try {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };
		SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
