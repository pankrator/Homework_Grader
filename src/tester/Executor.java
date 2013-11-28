package tester;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Executor {

	public static void main(String[] args) {

		int numberOfExercises = 3;
		String homeworkNumber = "3";
		String workingDirectory = "Z:/Niki SI/UP teaching/hw" + homeworkNumber;
		String tempResultFile = "Z:/Niki SI/UP teaching/temp.txt";
		String reportFile = workingDirectory + "/report.txt";

		Charset charset = Charset.forName("UTF-8");

		boolean doTest = true;

		File zipFilesDir = new File(workingDirectory);

		File[] zipFiles = zipFilesDir.listFiles();

		Runtime r = Runtime.getRuntime();
		try {
			BufferedWriter reportWriter = new BufferedWriter(new FileWriter(
					reportFile, false));
			
			for (File zip : zipFiles) {
				String extension = zip.getName().substring(
						zip.getName().lastIndexOf(".") + 1);
				if (extension.equals("zip")) {
					reportWriter.newLine();
					reportWriter.newLine();
					Path zipFilePath = Paths.get(zip.getAbsolutePath());
					try {
						reportWriter.append(zipFilePath.getFileName()
								.toString());
						reportWriter.append("\t");
						FileSystem zipFileSystem = FileSystems.newFileSystem(
								zipFilePath, null);
						EXERCISE: for (int i = 1; i <= numberOfExercises; i++) {
							new File(tempResultFile).createNewFile();
							reportWriter.append(String.valueOf(i) + "zad ");

							Path zadPath = zipFileSystem.getPath("/" + i
									+ "zad.cpp");

							byte[] code;
							try {
								code = Files.readAllBytes(zadPath);
							} catch (NoSuchFileException nsfe) {
								reportWriter.append("not found ");
								continue EXERCISE;
							}

			
							File tempDir = new File(workingDirectory + "/temp");

							if (!tempDir.exists())
								tempDir.mkdir();
							File zadFile = new File(workingDirectory + "/temp/"
									+ i + "zad.cpp");
							zadFile.createNewFile();
							FileWriter fWriter = new FileWriter(zadFile);
							BufferedWriter bfWrite = new BufferedWriter(fWriter);
							for (int k = 0; k < code.length; k++) {
								bfWrite.write((char) code[k]);
							}

							bfWrite.close();
							fWriter.close();

							String[] compile = new String[3];
							compile[0] = "cmd";
							compile[1] = "/c";


							compile[2] = "cd " + workingDirectory
									+ "/temp && z: && g++ " + i + "zad.cpp -o "
									+ i + "zad.exe";

			
							Process p = r.exec(compile);
							p.waitFor();

							InputStreamReader in = new InputStreamReader(
									p.getInputStream());
							BufferedReader bf = new BufferedReader(in);
							String line = bf.readLine();
							while (line != null) {
								System.out.println(line);
								if (line.contains("error")) {
//									doTest = false;
									reportWriter.append(" don't compile ");
									new File(tempResultFile).delete();
									continue EXERCISE;
									// break;
								}
								line = bf.readLine();
							}
							bf.close();
							in.close();

							if (doTest) {
								for (int j = 1; j <= 5; j++) {
									String inputFile = "input" + j + ".txt";
									String outputFile = "output" + j + ".txt";
									String[] execute = new String[3];
									execute[0] = "cmd";
									execute[1] = "/c";
									execute[2] = "cd " + workingDirectory
											+ "/temp" + " && z: && " + i
											+ "zad.exe < \"" + workingDirectory
											+ "/" + i + "zad/" + inputFile
											+ "\" > \"" + tempResultFile + "\"";
									Process e = r.exec(execute);
									e.waitFor();
									
									InputStreamReader in1 = new InputStreamReader(
											e.getInputStream());
									BufferedReader bf1 = new BufferedReader(in1);
									String line1 = bf1.readLine();
									while (line1 != null) {
										System.out.println(line1);
										line1 = bf1.readLine();
										if (line1.contains("error")) {
											System.out.println(line1);
											new File(tempResultFile).delete();
											continue EXERCISE;
										}

									}
									bf1.close();
									in1.close();

									BufferedReader outputReader = new BufferedReader(
											new FileReader(workingDirectory
													+ "/" + i + "zad/"
													+ outputFile));
									BufferedReader tempReader = new BufferedReader(
											new FileReader(tempResultFile));
									char[] content = new char[1024];
									char[] tempContent = new char[1024];

									boolean checking = true;
									String tLine, oLine = null;
									while ((tLine = tempReader.readLine()) != null) {
										tLine = tLine.trim();
										if (tLine.equals(""))
											continue;

										oLine = outputReader.readLine();

										if (!tLine.equals(oLine)) {
											checking = false;
											break;
										}
									}

									if (checking) {
										System.out.println("Arhiv "
												+ zipFilePath + " Zadacha " + i
												+ " Test " + j + " Poznat");
										if (j != 1)
											reportWriter.append("+");
										reportWriter.append("1");
									} else {
										System.out.println("Arhiv "
												+ zipFilePath + " Zadacha " + i
												+ " Test " + j + " nepoznat");
										if (j != 1)
											reportWriter.append("+");
										reportWriter.append("0");
									}


									outputReader.close();
									tempReader.close();
								}
								reportWriter.append(" ");
							}
							doTest = true;
							new File(tempResultFile).delete();
						}
//						reportWriter.newLine();
//						reportWriter.newLine();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			reportWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
