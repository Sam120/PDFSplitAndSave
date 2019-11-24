package com.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Custom built class to split .pdf files based on predefined string literal that can vary over multiple pages.
 * Wherever the literal varies split the .pdf there and create a new one.
 */
public class PDFLoadSplitSave {
	
	private static PDDocument loadPdfFile() {
		File file = new File("src/main/resources/SamplePDF.pdf");
		PDDocument loadedDoc = null;
		try {
			loadedDoc = PDDocument.load(file);
		} catch (IOException e) {
			System.out.println("PDF File could not be loaded.");
		}
		return loadedDoc;
	}
	
	public static void main(String[] args) throws IOException {
		//Reads the file
		PDDocument loadedDoc = loadPdfFile();
		
		PDFTextStripper reader = new PDFTextStripper();
		final int pages = loadedDoc.getNumberOfPages();
		
		//Initialize variables
		List<Integer> pageList = new ArrayList<Integer>();
		List<String> agtNoList = new ArrayList<String>();
		String agtNo = "";
		Pattern ptn = Pattern.compile("Agent No ([\\S]{6})");
		
		//Go through the document to find where to cut based on different agent no
		for(int i=1; i<=pages; i++) {
			reader.setStartPage(i);
			reader.setEndPage(i);
			final String text = reader.getText(loadedDoc);
			String nwAgtNo = "";
			
		    Matcher matcher = ptn.matcher(text);
		    while(matcher.find()) {
		    	nwAgtNo = matcher.group();
		    }
			
		    //final int index = text.indexOf("Agent No ")+9;
			//nwAgtNo = text.substring(index, (index+6));
			
			//Checks if old and new agent nos are equal
			if(nwAgtNo.equals(agtNo)) {
				continue;
			}else {
				pageList.add(i);
				agtNoList.add(nwAgtNo);
				agtNo = nwAgtNo;
			}
		}
		loadedDoc.close();
		
		for(int i=0; i<pageList.size(); i++) {
			if(i == pageList.size()-1) {
				//Directly saves the document in the HDD.
				split_SavePages(pageList.get(i), pages+1, agtNoList.get(i));
				
				//To save in database and then retrieve and save as .pdf in the HDD.
				splitPDFSaveByteCode(pageList.get(i), pages+1, i);
			}else {
				//Directly saves the document in the HDD.
				split_SavePages(pageList.get(i), pageList.get(i+1), agtNoList.get(i));
				
				//To save in database and then retrieve and save as .pdf in the HDD.
				splitPDFSaveByteCode(pageList.get(i), pageList.get(i+1), i);
			}
		}
	}
	
	/**
	 * Cuts the .pdf according to page no limits and saves as separate .pdf files
	 * 
	 * @param startPg
	 * @param endPg
	 * @param agtNo
	 * @throws IOException
	 */
	private static void split_SavePages(final int startPg, final int endPg, final String agtNo) throws IOException {
		PDDocument loadedDoc = loadPdfFile();
		PDDocument nwDoc = new PDDocument();
		for(int i = startPg-1; i < endPg-1; i++) {
			PDPage page = (PDPage) loadedDoc.getDocumentCatalog().getPages().get(i); 
			nwDoc.importPage(page);
	    }
		
		/**
		 * This creates the .pdf files (as AgentNo1.pdf, AgentNo2.pdf etc.) in the scr/main/output folder of the project.
		 * To view the file, run the code and then refresh the project and copy the file into your desktop.
		 * It is better to change this path into your preferred folder.
		 */
		nwDoc.save("src/main/output/"+agtNo+".pdf");
		nwDoc.close();
		loadedDoc.close();
	}
	
	/**
	 * Cuts the .pdf according to page no limits and convert and saves the byte array output stream
	 * 
	 * @param startPg
	 * @param endPg
	 * @param no
	 * @throws IOException
	 */
	private static void splitPDFSaveByteCode(final int startPg, final int endPg, final int no) throws IOException {
		PDDocument loadedDoc = loadPdfFile();
		PDDocument nwDoc = new PDDocument();
		for(int i = startPg-1; i < endPg-1; i++) {
			PDPage page = (PDPage) loadedDoc.getDocumentCatalog().getPages().get(i); 
			nwDoc.importPage(page);
	    }
		ByteArrayOutputStream bOS = new ByteArrayOutputStream();
		nwDoc.save(bOS);
		nwDoc.close();
		loadedDoc.close();
		getByteArrayOutputStreamToSavePDF(bOS, no);
	}
	
	/**
	 * Receives the byte array output stream and saves in pdf file 
	 * 
	 * @param bArr
	 * @param no
	 * @throws IOException
	 */
	private static void getByteArrayOutputStreamToSavePDF(ByteArrayOutputStream bOS, final int no) throws IOException {
		byte[] bArr = bOS.toByteArray();
		InputStream is = new ByteArrayInputStream(bArr);
		PDDocument doc = PDDocument.load(is);
		
		/**
		 * This creates the .pdf files (as 1.pdf, 2.pdf etc.) in the scr/main/output folder of the project.
		 * To view the file, run the code and then refresh the project and copy the file into your desktop.
		 * It is better to change this path into your preferred folder.
		 */
		doc.save("src/main/output/"+no+".pdf");
		doc.close();
	}
}
