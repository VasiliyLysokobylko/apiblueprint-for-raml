package com.mulesoft.raml.optimizer;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.raml.emitter.IRamlHierarchyTarget;
import org.raml.emitter.RamlEmitterV2;
import org.raml.model.MimeType;
import org.raml.model.Raml2;
import org.raml.model.RamlFileVisitorAdapter;
import org.raml.model.Resource;
import org.raml.parser.loader.FileResourceLoader;
import org.yaml.snakeyaml.parser.ParserException;

import com.mulesoft.jaxrs.raml.annotation.model.RAMLModelHelper;

public class ResourceOptimizer extends RAMLModelHelper {
	
	public ResourceOptimizer() {
		super();
	}

	public ResourceOptimizer(Raml2 raml) {
		super.coreRaml = raml;
	}

	public void optimizeRaml() {
		super.optimize();
		
		Map<String, Resource> container = new HashMap<String, Resource>();
		Iterator<Map.Entry<String, Resource>> resMapIterator = coreRaml.getResources().entrySet().iterator();
		while(resMapIterator.hasNext()){
			Map.Entry<String, Resource> entry = resMapIterator.next();
			Resource collapsedRes = collapseResources(entry.getValue());
			container.put(collapsedRes.getRelativeUri(), collapsedRes);
			resMapIterator.remove();
		}
		coreRaml.getResources().putAll(container);
		
	}
	
	protected void extractSchemaName(Raml2 coreRaml, MimeType mimeType) {
		String schemaOrigin = mimeType.getSchemaOrigin();
		String includePath=schemaOrigin;
		Map<String, String> schemaMap = coreRaml.getSchemaMap();
		int indexOf = schemaOrigin.indexOf('.');
		if (indexOf!=-1){
			schemaOrigin=schemaOrigin.substring(0,indexOf);
			int indexOf2 = schemaOrigin.indexOf('/');
			if (indexOf2!=-1){
				schemaOrigin=schemaOrigin.substring(indexOf2+1);
				if (schemaOrigin.endsWith("-schema")){ //$NON-NLS-1$
					schemaOrigin=schemaOrigin.substring(0,schemaOrigin.length()-"-schema".length()); //$NON-NLS-1$
				}
			}
		}
		if (schemaMap.containsKey(schemaOrigin)){
			schemaOrigin+="-schema"; //$NON-NLS-1$
		}
		schemaMap.put(schemaOrigin, includePath);
		List<Map<String, String>> schemas = coreRaml.getSchemas();
		HashMap<String, String>sm=new HashMap<String, String>();
		sm.put(schemaOrigin, mimeType.getSchema());
		mimeType.setSchema(schemaOrigin);
		schemas.add(sm);		
	}
	
	private void saveRaml(Raml2 raml, final String ramlPath) {

		final RamlEmitterV2 emitter = new RamlEmitterV2();
		final IRamlHierarchyTarget writer = new IRamlHierarchyTarget() {
			public void writeRoot(String content) {
				String filename = "api.raml";
				File folder = new File(ramlPath);
				File file = new File(folder, filename);
				try {
					saveFile(file, content);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			public void write(String path, String content) {
				if (path.contains("/")) {
					File folder = new File(ramlPath);
					File file = new File(folder, path);
					try {
						saveFile(file, content);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("path=" + path);
				}
			}
		};

		emitter.setSingle(true);
		emitter.dump(writer, raml);
	}
	
	protected void saveFile(File file, String content)
			throws FileNotFoundException, IOException,
			UnsupportedEncodingException {

		file.getParentFile().mkdirs();
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(content.getBytes("UTF-8"));
		bos.close();
	}
	
	protected Resource collapseResources (Resource parent){
		
		Iterator<Map.Entry<String, Resource>> resMapIterator = parent.getResources().entrySet().iterator();
		Map<String, Resource> container = new HashMap<String, Resource>();
		while(resMapIterator.hasNext()){
			Map.Entry<String, Resource> entry = resMapIterator.next();
			Resource collapsedRes = collapseResources(entry.getValue());
			container.put(collapsedRes.getRelativeUri(), collapsedRes);
			
			resMapIterator.remove();
		}
		parent.getResources().putAll(container);
		if ((parent.getResources().size() == 1) && (parent.getActions().size() == 0) && (parent.getType() == null)){
			Resource child = getChildResource(parent);
			String relativeUri = parent.getRelativeUri() + child.getRelativeUri();
			child.setRelativeUri(relativeUri);
			parent = child;
		}
		return parent;
	}
	
	private Resource getChildResource(Resource parent){
		Resource child = null;
		Iterator<Map.Entry<String, Resource>> resMapIterator = parent.getResources().entrySet().iterator();
		while(resMapIterator.hasNext()){
			Map.Entry<String, Resource> entry = resMapIterator.next();
			child = entry.getValue();
		}
		return child;
	}
	
	private void setCoreRaml(Raml2 raml){
		super.coreRaml = raml;
	}
}
