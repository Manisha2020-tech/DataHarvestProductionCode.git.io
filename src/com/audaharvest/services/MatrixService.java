package com.audaharvest.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.audaharvest.common.Matrix;
import com.audaharvest.constants.Constants;

public class MatrixService {
	//Build tags and parameters matrix
	public Matrix tagParamsMatrix(Document doc, String[] htmlTags, Map<String, String> map) {
		String[] params = new String[map.size()];
		params = map.keySet().toArray(params);
		int rows = htmlTags.length;
		int columns = params.length;		
		int[][] twoD = new int[rows][columns];
		System.out.println("Matrix size: [" +rows+ "x"+columns+"]");
		Matrix matrix = new Matrix(rows, columns);
		for(int i=0; i<rows; i++){
			//System.out.print("{");
			for(int j=0; j<columns; j++){
				Elements e = doc.select(htmlTags[i]+":matches(^"+params[j]+")");
				twoD[i][j] = e.size();
				//Uncomment to see the matrix
				//System.out.print(e.size());
				//System.out.print(",");
			}
			//System.out.println("}");
		}
		matrix.setData(twoD);
		return matrix;
	}
	
	//To find max value in the matrix row
	public int maxValue(int n, Matrix matrix) {
		List<Integer> rowList = new ArrayList<Integer>();
		for (int j=0; j<matrix.getNcols(); j++){
			rowList.add(matrix.getData()[n][j]);
		}
		return Collections.max(rowList);
		
	}
	//To get html tag containing maximum number of columns filled
	public Map<String, Integer> maxColsFilled(Matrix matrix, String[] htmlTags) {
		String tempStr = null;
		int tempCount = 0;
		int temp = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		for(int m=0; m<matrix.getNrows(); m++){
			int count= 0;
			for(int n=0; n<matrix.getNcols(); n++){
				if(matrix.getData()[m][n] != 0) {
					count++;
				}
			}
			//Uncomment to see which html tag contains maximum number of columns filled
			//System.out.println(htmlTags[m] + " tag has "+count +" columns filled");
			temp = count;
			if(temp > tempCount) {
			tempStr = htmlTags[m];
			tempCount = temp;
			}
		}
		
		map.put(tempStr, tempCount);
		return map;
	}
	
	
	public Map<String, String> unfilledCols(Matrix matrix, String[] htmlTags, String[] params){
		Map<String, String> map = new HashMap<String, String>();
		for(int m=0; m<matrix.getNrows(); m++) {
			for(int n=0; n<matrix.getNcols(); n++){
				if(matrix.getData()[m][n] == 0){
					map.put(htmlTags[m], params[n]);
				}
			}
		}
		return map;
		
	}
	
	private Matrix verifyData(int rows, int columns, Document doc, String cssSelect) {		
		int[][] twoD = new int[rows][columns];
		Matrix matrix = new Matrix(rows, columns);
		
		for(int i=0; i<rows; i++){
			//System.out.print("{");
			for(int j=0; j<columns; j++){
				Elements e = doc.select(cssSelect);
				twoD[i][j] = e.size();
				//Uncomment to see the matrix
				//System.out.print(e.size());
				//System.out.print(",");
			}
			//System.out.println("}");
		}
		matrix.setData(twoD);
		
		return matrix;
	}
	
}
