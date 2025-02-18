package gr.uoi.cs.dbsea.stylecore;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import gr.uoi.cs.dbsea.columnsstylecheck.ColumnCheck;
import gr.uoi.cs.dbsea.columnsstylecheck.UniformSuffixes;
import gr.uoi.cs.dbsea.generalchecks.ReservedWords;
import gr.uoi.cs.dbsea.gui.Worker;
import gr.uoi.cs.dbsea.logger.Logger;
import gr.uoi.cs.dbsea.parser.SESParser;
import gr.uoi.cs.dbsea.sql.Attribute;
import gr.uoi.cs.dbsea.sql.Schema;
import gr.uoi.cs.dbsea.sql.Table;
import gr.uoi.cs.dbsea.statistics.DatasetStatistics;
import gr.uoi.cs.dbsea.tablestylecheck.TableCheck;
import gr.uoi.cs.dbsea.tablestylecheck.TablePrefixes;

public class SchemaStyleAnalysis {

	public String path;
	public int versions;

	/**
	 * @param result
	 * @param folder
	 * @return
	 * @throws IOException
	 */
	public void checkSchemaHistoryStyleByRuleAndExport(File folder) throws IOException {
		try {

			// Schema Evolution Suite
			TableCheck tableCheck = new TableCheck();
			ColumnCheck columnCheck = new ColumnCheck();
			path = folder.getAbsolutePath();

			String[] folders = folder.list();

			String pattern = Pattern.quote(System.getProperty("file.separator"));
			String columnPath = "Results\\SchemaLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
					+ path.split(pattern)[path.split(pattern).length - 3]
					+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv";
			String tablePath = "Results\\SchemaLevelAnalysis\\TableStatistics\\TableStatistics-"
					+ path.split(pattern)[path.split(pattern).length - 3]
					+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv";
			columnCheck.setStatisticsFile(columnPath, columnPath);
			columnCheck.setFileTitle(columnPath);

			tableCheck.setStatisticsFile(tablePath, tablePath);
			tableCheck.SetFileTitle(tablePath);

			TablePrefixes.setUpListWithPrefixes();
			UniformSuffixes.SetUpListWithSuffixes();

			java.util.Arrays.sort(folders);

			ReservedWords.SetReservedWords();
			for (int i = 0; i < folders.length - 1; i++) {

				System.out.println(path + File.separator + folders[i]);
				Schema schemaA = getSchema(path + File.separator + folders[i]);

				ArrayList<String> tablesInSchema = new ArrayList<String>();

				try {
					for (Entry<String, Table> e : schemaA.getTables().entrySet()) {

						String tableName = e.getKey();
						for (Attribute columnName : e.getValue().getAttrs().values()) {

							try {
								columnCheck.runChecks(columnName.getName(), tableName);
							} catch (Exception ex) {
								ex.fillInStackTrace();
								ex.getStackTrace();
								System.out.println("EXCEPTION : " + ex.getMessage());
							}
						}
						tableCheck.runChecks(tableName);
						tablesInSchema.add(tableName);
					}
				} catch (Exception e) {

					Logger.Log(e);

				}
				tableCheck.nameConcatenation(tablesInSchema);
				tableCheck.dataSetName = path.split(pattern)[path.split(pattern).length - 2];
				columnCheck.dataSetName = path.split(pattern)[path.split(pattern).length - 2];
				columnCheck.writeStatistics(columnPath);
				columnCheck.clearStatistics();
				tableCheck.writeStatistics(tablePath);
				tableCheck.clearStatistics();

			}
			columnCheck.clearStatistics();
			tableCheck.clearStatistics();

			DatasetStatistics a = new DatasetStatistics(tablePath, columnPath);
			a.FillMeasurements();

			System.out.println("Finished!");
		} catch (Exception e) {

			Logger.Log(e);

		}
	}

	public void checkSchemaHistoryStyleByTableAndExport(File folder) throws IOException {
		try {

			TableCheck tableCheck = new TableCheck();
			ColumnCheck columnCheck = new ColumnCheck();
			path = folder.getAbsolutePath();

			String[] folders = folder.list();
			String pattern = Pattern.quote(System.getProperty("file.separator"));
			columnCheck.setStatisticsFile(
					"Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv",
					"Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
			columnCheck.setFileTitle("Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
					+ path.split(pattern)[path.split(pattern).length - 3]
					+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
			tableCheck.setStatisticsFile(
					"Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv",
					"Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");

			tableCheck.SetFileTitle("Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
					+ path.split(pattern)[path.split(pattern).length - 3]
					+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");

			TablePrefixes.setUpListWithPrefixes();
			UniformSuffixes.SetUpListWithSuffixes();

			java.util.Arrays.sort(folders);

			ReservedWords.SetReservedWords();
			for (int i = 0; i < folders.length - 1; i++) {
				// result.clear();
				 
				System.out.println(path + File.separator + folders[i]);
				Schema schemaA = getSchema(path + File.separator + folders[i]);

				ArrayList<String> tablesInSchema = new ArrayList<String>();

				try {
					for (Entry<String, Table> e : schemaA.getTables().entrySet()) {

						String tableName = e.getKey();
						for (Attribute columnName : e.getValue().getAttrs().values()) {

							try {
								columnCheck.runChecks(columnName.getName(), tableName);
							} catch (Exception ex) {
								ex.fillInStackTrace();
								ex.getStackTrace();
								System.out.println("EXCEPTION : " + ex.getMessage());
							}
						}
						tableCheck.runChecks(tableName);
						tablesInSchema.add(tableName);
						tableCheck.nameConcatenation(tablesInSchema);
						tableCheck.dataSetName = tableName;
						columnCheck.dataSetName = tableName;

						columnCheck.writeStatistics("Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
								+ path.split(pattern)[path.split(pattern).length - 3]
								+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
						columnCheck.clearStatistics();
						tableCheck.writeStatistics("Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
								+ path.split(pattern)[path.split(pattern).length - 3]
								+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
						tableCheck.clearStatistics();

					}
				} catch (Exception e) {
					e.fillInStackTrace();
					e.getStackTrace();
				}

				columnCheck.IncreaseRevisionIndex("Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
						+ path.split(pattern)[path.split(pattern).length - 3]
						+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
				tableCheck.IncreaseRevisionIndex("Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
						+ path.split(pattern)[path.split(pattern).length - 3]
						+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
			}

			columnCheck.clearStatistics();
			tableCheck.clearStatistics();
			DatasetStatistics a = new DatasetStatistics(
					"Results\\RuleLevelAnalysis\\TableStatistics\\TableStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv",
					"Results\\RuleLevelAnalysis\\ColumnStatistics\\ColumnsStatistics-"
							+ path.split(pattern)[path.split(pattern).length - 3]
							+ path.split(pattern)[path.split(pattern).length - 2] + "-.csv");
			a.FillMeasurements();

			System.out.println("Finished!");
		} catch (Exception e) {

			Logger.Log(e);

		}
	}

	public void traversePaths(File folder) throws IOException {
		String[] folders = folder.list();
		String path = folder.getAbsolutePath();
		if (folders == null) {
			return;
		}
		String fileName = "Results";

		Path resultsPath = Paths.get(fileName);

		if (!Files.isDirectory(resultsPath)) {
			Files.createDirectory(resultsPath);

			Path statistics = Paths.get("Results\\SchemaLevelAnalysis");
			Files.createDirectories(statistics);

			statistics = Paths.get("Results\\SchemaLevelAnalysis\\ColumnStatistics");
			Files.createDirectories(statistics);

			statistics = Paths.get("Results\\SchemaLevelAnalysis\\TableStatistics");
			Files.createDirectories(statistics);

			statistics = Paths.get("Results\\RuleLevelAnalysis\\ColumnStatistics");
			Files.createDirectories(statistics);

			statistics = Paths.get("Results\\RuleLevelAnalysis\\TableStatistics");
			Files.createDirectories(statistics);

		}

		// Where the GUI is constructed:

		for (int i = 0; i < folders.length; i++) {

			if (folders[i].equals("schemata")) {

				File myFile = new File(path + File.separator + folders[i]);

				// checkDifferencesInSchemataHistoryAndExport(myFile);
				checkSchemaHistoryStyleByTableAndExport(myFile);
				checkSchemaHistoryStyleByRuleAndExport(myFile);
			} else {
				File myFile = new File(path + File.separator + folders[i]);

				traversePaths(myFile);
			}
		}
	}

	/**
	 * @param csv
	 * @param xml
	 * @param metrics
	 * @throws IOException
	 */

	public Schema getSchema(String path) {
		return SESParser.parse(path);
	}
}
