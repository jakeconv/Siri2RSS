package net.mtabuscis.siri2rss.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.mtabuscis.siri2rss.input.services.RawDataService;

//this class simply reads files into a string
public class GenericFileReader implements RawDataService {

	private static Logger _log = LoggerFactory.getLogger(GenericFileReader.class);
	
	private URI _fileName = null;
	
	private BufferedReader _dumpFileReadHandle = null;
	
	//the contents of the siri file could be large, string buffers perform well with large data
	private StringBuffer _data = null;
	
	@Override
	public void load(URI source) {
		//minor error detection
		if(source == null)
			throw new RuntimeException("No file to load");
		
		if(source.toASCIIString().length() < 1)
			throw new RuntimeException("The file to load was too short... is it a file?");
		
		//source is a file location on the local machine in this instance
		_fileName = source;
		
		//read the file properly
		openFile();
		retrieveData();
		closeFile();
	}
	
	private void openFile() throws RuntimeException{
		
		if(_fileName == null){
			_log.error("The file name was blank.  Try loading the file first.");
			return;
		}
		
		_log.info("Loading file "+_fileName);
		
		//initialize file reader (open the file)
		File file = new File(_fileName);
		
		try {
			_dumpFileReadHandle = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("The file could not be opened: "+_fileName);
		}
		
	}
	
	private void retrieveData() throws RuntimeException{
		if(_dumpFileReadHandle == null)
			throw new RuntimeException("The file handle wasn't loaded");
		
		_data = new StringBuffer();
		
		String line;
		try {
			while((line = _dumpFileReadHandle.readLine()) != null) {
				_data.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The data could not be read on file: "+_fileName);
		}
	}
	
	private void closeFile(){
		if(_dumpFileReadHandle == null)
			throw new RuntimeException("The file handle wasn't open to close");
		
		try {
			_dumpFileReadHandle.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The file handle couldn't be closed due to an IOException");
		}
	}

	@Override
	public String getContentsAsString() {
		if(_data != null)
			return _data.toString();
		return "";//just return a blank string
	}

}
