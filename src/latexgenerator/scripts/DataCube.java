package latexgenerator.scripts;

import java.util.stream.IntStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import joptsimple.*;

public class DataCube
{
	private static final File OUTPUT = new File("tex/output.tex");

	public static void main(String[] args) throws IOException
	{
		OptionParser parser = new OptionParser();
		OptionSpec<Integer> columnSpec = parser.accepts( "columns" ).withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> rowSpec = parser.accepts( "rows" ).withRequiredArg().ofType(Integer.class);
		OptionSpec<Integer> layersSpec = parser.accepts( "layers" ).withRequiredArg().ofType(Integer.class);

		if(args.length==0)
		{
			System.out.println("Data Cube Latex Generator, no options given, using defaults. For next time:");
			parser.printHelpOn( System.out );
			args = new String[] {"-c2","-r4","-l3"};
		}
		OptionSet options = parser.parse(args);
		int columns = options.valueOf(columnSpec);
		int rows = options.valueOf(rowSpec);
		int layers = options.valueOf(layersSpec);
		int measures = 2;
		
		String xValues = IntStream.range(1, columns+1).mapToObj(i->"xvalue"+i).reduce((a,b)->a+","+b).get();
		String yValues = IntStream.range(1, rows+1).mapToObj(i->"yvalue"+i).reduce((a,b)->a+","+b).get();
		String zValues = IntStream.range(1, layers+1).mapToObj(i->"zvalue"+i).reduce((a,b)->a+","+b).get();
		String matrix =
				IntStream.range(1, rows*measures+1).mapToObj(y->
				IntStream.range(1, columns+1).mapToObj(x->"m-"+y+"-"+x).reduce((a,b)->a+"	&"+b).get()+"\\\\").reduce((a,b)->a+"\n"+b).get();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Files.copy(new File("tex/datacube.template.tex").toPath(), baos);		
		String template = baos.toString(StandardCharsets.UTF_8.name());
		template = template.replace("§LAYERS§", String.valueOf(layers));
		template = template.replace("§COLUMNS§", String.valueOf(columns));
		template = template.replace("§COLUMNS-1§", String.valueOf(columns-1));
		template = template.replace("§ROWS§", String.valueOf(rows));
		template = template.replace("§ROWS*MEASURES§", String.valueOf(rows*measures));
		template = template.replace("§ROWS*MEASURES-1§", String.valueOf(rows*measures-1));
		template = template.replace("§ROWS*MEASURES+1§", String.valueOf(rows*measures+1));
		template = template.replace("§XVALUES§", xValues);
		template = template.replace("§YVALUES§", yValues);
		template = template.replace("§ZVALUES§", zValues);
		template = template.replace("§MATRIX§", matrix);

		if(template.contains("§")) throw new RuntimeException("Unreplaced template: "
		+template.substring(template.indexOf('§'),template.indexOf('§')+130));
		
		try(FileWriter out = new FileWriter(OUTPUT))
		{out.write(template);}
		System.out.println("output written to  "+OUTPUT);
	}
}