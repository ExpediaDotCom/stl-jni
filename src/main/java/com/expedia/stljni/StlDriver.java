package com.expedia.stljni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class StlDriver {
	private static final String OPT_NP = "np";
	private static final String OPT_NS = "ns";
	private static final String OPT_NT = "nt";
	private static final String OPT_NL = "nl";
	private static final String OPT_NO = "no";
	private static final String OPT_NI = "ni";
	
	public static void main(String[] args) throws IOException {
		CommandLine cli = buildCommandLine(args);
		final String filename = args[0];
		float[] y = readY(filename);
		StlParams params = buildParams(y, cli);
		Stl stl = new Stl(params);
		StlResult result = stl.decompose(y);
		dumpResult(y, result);
	}
	
	private static CommandLine buildCommandLine(String[] args) {
		Options options = new Options();
		options.addOption(requiredIntOption(OPT_NP, "number of observations per period"));
		options.addOption(requiredIntOption(OPT_NP, "number of observations per period"));
		options.addOption(requiredIntOption(OPT_NS, "length of the seasonal smoother"));
		options.addOption(requiredIntOption(OPT_NT, "length of the trend smoother"));
		options.addOption(requiredIntOption(OPT_NL, "length of the low-pass filter"));
		options.addOption(requiredIntOption(OPT_NO, "number of outer loop iterations"));
		options.addOption(requiredIntOption(OPT_NI, "number of inner loop iterations"));
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine commandLine = parser.parse(options, args);
			return commandLine;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	// For now, required option.
	private static Option requiredIntOption(String optName, String desc) {
		// @formatter:off
		return Option.builder(optName)
				.type(Integer.class)
				.hasArg(true)
				.required(true)
				.desc(desc)
				.build();
		// @formatter:on
	}
	
	private static float[] readY(String filename) throws IOException {
		List<Float> counts = new ArrayList<>();
		File file = new File(filename);
		FileInputStream fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		
		while ((line = br.readLine()) != null) {
			float value = Float.parseFloat(line);
			counts.add(value);
		}
		br.close();

		int n = counts.size();
		float[] y = new float[n];
		for (int i = 0; i < n; i++) {
			y[i] = counts.get(i);
		}
		
		return y;
	}
	
	/**
	 * Requires y because default values depend on y.
	 * 
	 * @param y
	 * @param args
	 * @return
	 */
	private static StlParams buildParams(float[] y, CommandLine cli) {
		
		// TODO Implement default values
		// nt = smallest odd integer greater than or equal to
		//                     (1.5*np) / (1-(1.5/ns)).
		// nl = smallest odd integer greater than or equal to np.
		// nsjump = ns/10; ntjump = nt/10; nljump = nl/10.
		// ni = 2 if robust=.false.; else ni=1.
		// no = 0 if robust=.false.; else robustness iterations are  carried
		// out until convergence of both seasonal and trend components, with
		// 15 iterations maximum.  Convergence occurs if the maximum changes
		// in  individual  seasonal  and  trend fits are less than 1% of the
		// component's range after the previous iteration.
		
		final StlParams params = new StlParams();
		params.np = Integer.parseInt(cli.getOptionValue(OPT_NP));
		params.ns = Integer.parseInt(cli.getOptionValue(OPT_NS));
		params.nt = Integer.parseInt(cli.getOptionValue(OPT_NT));
		params.nl = Integer.parseInt(cli.getOptionValue(OPT_NL));
		params.isdeg = 1;
		params.itdeg = 1;
		params.ildeg = 1;
		params.nsjump = params.ns / 10;
		params.ntjump = params.nt / 10;
		params.nljump = params.nl / 10;
		params.no = Integer.parseInt(cli.getOptionValue(OPT_NO));
		params.ni = Integer.parseInt(cli.getOptionValue(OPT_NI));
		return params;
	}
	
	private static void dumpResult(float[] y, StlResult result) {
		System.out.println("y,trend,seasonal");
		for (int i = 0; i < y.length; i++) {
			System.out.println(y[i] + "," + result.trend[i] + "," + result.season[i]);
		}
	}
}
