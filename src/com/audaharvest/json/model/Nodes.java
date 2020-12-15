package com.audaharvest.json.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Nodes {
	
	@JsonProperty("make")
    private Make make;
	@JsonProperty("model")
    private Model model;
	@JsonProperty("year")
    private Integer year;
	@JsonProperty("id")
    private Integer id;
	
	
	public Make getMake() {
		return make;
	}
	public void setMake(Make make) {
		this.make = make;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public class Make {		
		@JsonProperty("name")
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}		
	}
	
	public class Model {
		@JsonProperty("name")
	    private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}		
		
	}

}
