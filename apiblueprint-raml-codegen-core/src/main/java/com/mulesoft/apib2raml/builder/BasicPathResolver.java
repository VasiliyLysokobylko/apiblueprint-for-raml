package com.mulesoft.apib2raml.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;

public class BasicPathResolver implements IPathResolver {
	
	private File root;
	private File outputRoot;

	public BasicPathResolver(File root, File outputRoot) {
		this.root = root;
		this.outputRoot = outputRoot;
	}

	public String getContent(String path) {
		if (path.startsWith("http:") || path.startsWith("https:")){
			URL url;
			try {
				url = new URL(path);

				StringWriter wr = new StringWriter();

				// Read all the text returned by the server
				BufferedReader in = new BufferedReader(new InputStreamReader(
						url.openStream()));
				char[] cbuf = new char[2048];
				int l = 0;
				while ((l = in.read(cbuf)) > 0) {
					wr.write(cbuf, 0, l);
				}
				in.close();
				String result = wr.toString();

				return result;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		else{
			File file = new File(root, path);
			InputStream in;
			try {
				in = new FileInputStream(file);
				String content = IOUtils.toString(in);
				return content;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public String getOutputRootPath() {
		return outputRoot.getPath();
	}
}
