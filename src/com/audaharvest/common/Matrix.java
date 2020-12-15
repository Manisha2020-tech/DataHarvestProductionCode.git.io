package com.audaharvest.common;

public class Matrix {
	private int nrows;
	private int ncols;
	private int[][] data;
	
	public Matrix(){
		
	}
	
	public Matrix(int nrow, int ncol) {
		this.nrows=nrow;
		this.ncols=ncol;
		this.data = new int[nrow][ncol];
	}
	
	public Matrix(int rows, int cols, int[][] data) {
		this.nrows=rows;
		this.ncols=cols;
		for(int i=0; i<nrows; i++){
			for(int j=0; j<ncols;j++){
				this.data[i][j] = data[i][j];
			}
		}
	}

	public int getNrows() {
		return nrows;
	}

	public void setNrows(int nrows) {
		this.nrows = nrows;
	}

	public int getNcols() {
		return ncols;
	}

	public void setNcols(int ncols) {
		this.ncols = ncols;
	}

	public int[][] getData() {
		return data;
	}

	public void setData(int[][] data) {
		this.data = data;
	}
	
}
