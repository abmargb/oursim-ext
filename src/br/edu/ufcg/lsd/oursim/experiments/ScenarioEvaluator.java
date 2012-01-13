package br.edu.ufcg.lsd.oursim.experiments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

public class ScenarioEvaluator {

	static String SCENARIO = "resources/experiments/remote-nof";
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		File root = new File(SCENARIO);
		analyzeFolder(root, new StringBuilder());
		
	}

	private static void analyzeFolder(File root, StringBuilder stringBuilder) throws FileNotFoundException, IOException {
		boolean onlyFiles = true;
		
		for (File file : root.listFiles()) {
			if (file.isDirectory()) {
				StringBuilder strBuilderCopy = new StringBuilder(stringBuilder.toString());
				strBuilderCopy.append(file.getName()).append(" ");
				analyzeFolder(file, strBuilderCopy);
				onlyFiles = false;
			} else {
				analyzeFile(file);
			}
		}
		
		if (onlyFiles) {
			System.out.println(stringBuilder.toString());
		}
	}

	private static void analyzeFile(File file) throws FileNotFoundException, IOException {
		List<String> lines = IOUtils.readLines(new FileInputStream(file));
		Map<String, Long> greaterMakespan = new HashMap<String, Long>();
		
		for (String line : lines) {
			String[] split = line.split(":");
			if (split[0].equals("JOB_ENDED")) {
				String broker = split[4];
				Long makespan = Long.valueOf(split[1]);
				
				Long currentMakespan = greaterMakespan.get(broker);
				if (currentMakespan == null) {
					greaterMakespan.put(broker, makespan);
				} else {
					greaterMakespan.put(broker, Math.max(makespan, currentMakespan));
				}
				
			}
		}
		StringBuilder b = new StringBuilder();
		for (Entry<String, Long> msEntry : greaterMakespan.entrySet()) {
			b.append(msEntry.getValue() + "\t");
		}
		
		System.out.println(b.toString().trim());
	}
	
}
