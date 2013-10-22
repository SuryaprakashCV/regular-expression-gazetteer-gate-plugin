package com.sf.textlytics.gate;

import java.util.*; 
import gate.*; 
import gate.creole.*; 
import gate.creole.metadata.CreoleResource;
import gate.util.*; 
import static gate.Utils.*;
import java.io.*;
import java.util.Scanner;
import java.net.URL;
import gate.Annotation;
import gate.AnnotationSet;
import gate.DocumentContent;
import gate.ProcessingResource;
import gate.Resource;
import gate.creole.AbstractLanguageAnalyser;
@CreoleResource(name = "RegexGazetteer", 
	       comment = "Regex Based Gazetteer")
public @SuppressWarnings("all")  class RegexGazetteer  extends AbstractLanguageAnalyser implements ProcessingResource {

	URL model;
	String strFileName = "";
	String gazetteerFeatureSeparator = "";
	String strEncoding = "UTF-8";
	StringBuilder strBufFileContent = new StringBuilder();
	String strLineSeperator = System.getProperty("line.separator");
	String outputASName = "";
	private static final long serialVersionUID = 1L;
	String inputASName;

	public String getInputASName() {
		return inputASName;
	}

	public void setInputASName(String inputASName) {
		this.inputASName = inputASName;
	}
	
	public void setModel(URL model) {		
		this.model = model;		
	}
	
	public URL getModel() {
		return model;
	}
	
	public void setOutputASName(String outputAsName) {
		outputASName = outputAsName;
	}

	public String getOutputASName() {
		return outputASName;
	}
	

	public String getGazetteerFeatureSeparator() {
	    return gazetteerFeatureSeparator;
	  }
	  
	public void setGazetteerFeatureSeparator(String gazetteerFeatureSeparator) {
	    this.gazetteerFeatureSeparator = gazetteerFeatureSeparator;
	  }
	
	String annotationType;

	public String getAnnotationType() {
		return annotationType;
	  }

	public void setAnnotationType(String annotationType) {
		this.annotationType = annotationType;
	  }

	public void execute() 
	{
		try
		{	
			FeatureMap features = Factory.newFeatureMap();	
			DocumentContent content = document.getContent();
			AnnotationSet bindings = document.getAnnotations(inputASName);
			AnnotationSet outBindings = document.getAnnotations(outputASName);
			AnnotationSet sent = bindings.get(annotationType);	
			List<Annotation>  sentList= new ArrayList<Annotation>(sent);
			String[] strArrayFileContent = this.strBufFileContent.toString().split("(\n)");
			for(int index=0; index<strArrayFileContent.length; index++)
			{
				if(this.gazetteerFeatureSeparator == "")
					this.gazetteerFeatureSeparator = "&";
				String[] regexArray = strArrayFileContent[index].toString().split("("+this.gazetteerFeatureSeparator+")");
				for (int sentIndex=0; sentIndex < sentList.size(); sentIndex++) 
				{
					Annotation Lookup = sentList.get(sentIndex);
					String sentence = content.getContent(sentList.get(sentIndex).getStartNode().getOffset(), sentList.get(sentIndex).getEndNode().getOffset()).toString();
					java.util.regex.Pattern regexPattern = java.util.regex.Pattern.compile(regexArray[0].toString());
					java.util.regex.Matcher regexMatcher = regexPattern.matcher(sentence);
					while(regexMatcher.find())
					{
						Long startOffset = sentList.get(sentIndex).getStartNode().getOffset()+regexMatcher.start();
						Long endOffset = sentList.get(sentIndex).getStartNode().getOffset()+regexMatcher.end();
						features = addfeatures(regexArray);
						outBindings.add(startOffset, endOffset, "Lookup", features);
					}
				}
		    }
		}
		catch(Exception e)
		{
		   e.printStackTrace();
		}		
	}
	
	protected FeatureMap addfeatures(String[] feature){
		FeatureMap features = Factory.newFeatureMap();	
		for(int i=1;i<feature.length;i++ )
		{
			String[] featureList = feature[i].split("("+this.gazetteerFeatureSeparator+")");
			String featureName = featureList[0].split("(=)")[0];
			String featureValue = featureList[0].split("(=)")[1];
			features.put(featureName,featureValue);
		}
		return features;
	}
	
	@Override
	public Resource init() throws ResourceInstantiationException {
		try{
			    this.strFileName = model.toString().replaceAll(("(file:)"), "");
			    Scanner scanner = new Scanner(new FileInputStream(this.strFileName), strEncoding);
			    try
			    {
			    	while (scanner.hasNextLine()){
			    		this.strBufFileContent.append(scanner.nextLine() + strLineSeperator);
				    }
			    	System.out.println("file: "+this.strFileName);
				}
			    finally{
			        scanner.close();
			    }
	    }
		catch (Exception e) {
		}
	return this;
	}
	
	@Override
	public void reInit() throws ResourceInstantiationException {
		init();		
	}	
}

