package br.edu.ufcg.lsd.oursim.experiments;

import java.io.File;
import java.io.PrintWriter;

import jdepend.framework.PackageFilter;
import jdepend.textui.JDepend;

public class JDependRunner {

	public static void main(String[] args) throws Exception {
		JDepend jdepend = new JDepend();
		PackageFilter filter = new PackageFilter();
		filter.addPackage("br.edu.ufcg.lsd.oursim.acceptance");
		jdepend.setFilter(filter);
		
		jdepend.addDirectory("bin");
		jdepend.setWriter(new PrintWriter(new File("report.txt")));
		jdepend.analyze();
	}
	
}
