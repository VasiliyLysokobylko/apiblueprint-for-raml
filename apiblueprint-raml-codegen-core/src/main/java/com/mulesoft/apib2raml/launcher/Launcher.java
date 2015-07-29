package com.mulesoft.apib2raml.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.mulesoft.raml.builder.RamlBuilder;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;
import org.raml.emitter.IRamlHierarchyTarget;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.Raml2;

import com.mulesoft.apib2raml.builder.BasicPathResolver;
import com.mulesoft.apib2raml.builder.BuildManager;
import com.mulesoft.apib2raml.model.ApplicationModel;
import com.mulesoft.raml.optimizer.ResourceOptimizer;

public class Launcher {

	public static void main(String[] args) {
        HashMap<String,String> argsMap = parseArgs(args);
        
        String inputFilePath = argsMap.get("input");        
        String outputFilePath = argsMap.get("output");
        
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        
        try {
            process(inputFile, outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    private static void process(File inputFile, File outputFile) throws Exception {
        
    	BasicPathResolver pathResolver = new BasicPathResolver(inputFile.getParentFile(), outputFile.getParentFile());
    	
        BuildManager buildManger = new BuildManager();
        buildManger.setPathResolver(pathResolver);
        
        RamlBuilder ramlBuilder = new RamlBuilder();
        
        String content = readFile(inputFile);
        PegDownProcessor processor = new PegDownProcessor();
        RootNode rootNode = processor.parseMarkdown(content.toCharArray());
        
        ApplicationModel app = buildManger.process(rootNode);
        Raml2 raml = ramlBuilder.buildRaml(app);
        
        ResourceOptimizer resourceOptimizer = new ResourceOptimizer(raml);
        resourceOptimizer.optimizeRaml();
        
        saveRaml(outputFile, raml);
    }

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String,String> map = new HashMap<String, String>();
        for(int i = 0 ; i < args.length ; i+=2){
            String key = args[i];
            if(key.startsWith("-")){
                key=key.substring(1);
            }
            if(i<args.length-1){
                String value = args[i+1];
                map.put(key, value);
            }
        }
        return map;
    }
    
	protected static String readFile(File file) {
		FileInputStream fis;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buff = new byte[2048];

			int l = 0;
			while ((l = bis.read(buff)) >= 0) {
				baos.write(buff, 0, l);
			}
			bis.close();

			String result = new String(baos.toByteArray(), "UTF-8");
			return result;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private static void saveRaml(final File outputFile, Raml2 raml)
	{
    	final File root = outputFile.getParentFile();
    	
		RamlEmitterV2 emmitter = new RamlEmitterV2();
		emmitter.dump(new IRamlHierarchyTarget() {
			
			public void writeRoot(String content) {
				try {
					saveFile(content, outputFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			public void write(String path, String content) {
				File f = new File(root, path);
				f.getParentFile().mkdirs();
				try {
					saveFile(content, f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		},raml);
	}
	
    private static void saveFile(String content, File file) throws Exception {
        
        if(!file.exists()){
        	file.getParentFile().mkdirs();
            file.createNewFile();
        }
        
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(content.getBytes("UTF-8"));
        bos.close();
    }
}

