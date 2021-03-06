package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import basicAnalysis.FIFONP;
import basicAnalysis.FIFOP;
import basicAnalysis.NewMrsPRTA;
import basicAnalysis.NewMrsPRTAWithMC;
import basicAnalysis.NewMrsPRTAWithMCNP;
import entity.Resource;
import entity.SporadicTask;
import generatorTools.SystemGenerator;
import generatorTools.SystemGenerator.CS_LENGTH_RANGE;
import generatorTools.SystemGenerator.RESOURCES_RANGE;

public class MIgrationCostSchedTest {

	public static int TOTAL_NUMBER_OF_SYSTEMS = 1000;
	public static int TOTAL_PARTITIONS = 16;
	public static int MIN_PERIOD = 1;
	public static int MAX_PERIOD = 1000;
	public static int MIGRATION_COST = 6;

	public static void main(String[] args) throws InterruptedException {
		// int experiment = 0;
		// int bigSet = 0;
		// int smallSet = 0;
		//
		// if (args.length == 3) {
		// experiment = Integer.parseInt(args[0]);
		// bigSet = Integer.parseInt(args[1]);
		// smallSet = Integer.parseInt(args[2]);
		//
		// switch (experiment) {
		// case 1:
		// experimentIncreasingWorkLoad(bigSet, smallSet);
		// break;
		// case 2:
		// experimentIncreasingCriticalSectionLength(bigSet, smallSet);
		// break;
		// case 3:
		// experimentIncreasingContention(bigSet, smallSet);
		// break;
		// default:
		// break;
		// }
		//
		// } else
		// System.err.println("wrong parameter.");

		// for (int i = 1; i < 6; i++) {
		// for (int j = 1; j < 10; j++) {
		// experimentIncreasingWorkLoad(i, j);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// for (int i = 1; i < 4; i++) {
		// for (int j = 1; j < 6; j++) {
		// experimentIncreasingCriticalSectionLength(i, j);
		// }
		// System.out.println();
		// }
		// System.out.println();
		// System.out.println();
		// System.out.println();
		// for (int i = 1; i < 4; i++) {
		// for (int j = 1; j < 11; j++) {
		// experimentIncreasingContention(i, j);
		// }
		// System.out.println();
		// }

		// for (int j = 1; j < 10; j++) {
		// experimentIncreasingWorkLoad(1, j);
		// }

		// for (int j = 1; j <= 300; j++) {
		// experimentIncreasingCriticalSectionLength(2, j);
		// }

		// for (int j = 1; j < 10; j++) {
		// experimentIncreasingContention(2, j);
		// }
		//
		for (int j = 2; j < 33; j++) {
			experimentIncreasingParallel(j);
		}

		System.out.println();

		ResultReader.schedreader();
		ResultReader.migReader();
	}

	public static void experimentIncreasingParallel(int partitions) {
		double RESOURCE_SHARING_FACTOR = 0.4;
		int NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE = 3;
		int NUMBER_OF_TASKS_ON_EACH_PARTITION = 4;
		int total_partitions = partitions;

		SystemGenerator generator = new SystemGenerator(MIN_PERIOD, MAX_PERIOD, 0.1 * (double) NUMBER_OF_TASKS_ON_EACH_PARTITION, total_partitions,
				NUMBER_OF_TASKS_ON_EACH_PARTITION, true, CS_LENGTH_RANGE.SHORT_CS_LEN, RESOURCES_RANGE.PARTITIONS, RESOURCE_SHARING_FACTOR,
				NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE);
		long[][] Ris;
		NewMrsPRTA mrsp = new NewMrsPRTA();
		NewMrsPRTAWithMC mrsp_mig = new NewMrsPRTAWithMC();
		NewMrsPRTAWithMCNP mrsp_np = new NewMrsPRTAWithMCNP();
		FIFOP fp = new FIFOP();
		FIFONP fnp = new FIFONP();

		String result = "";
		int smrsp = 0;
		int smrsp_mig = 0;
		int fifop = 0;
		int fifonp = 0;
		int smrsp_np = 0;

		for (int i = 0; i < TOTAL_NUMBER_OF_SYSTEMS; i++) {
			ArrayList<ArrayList<SporadicTask>> tasks = generator.generateTasks();
			ArrayList<Resource> resources = generator.generateResources();
			generator.generateResourceUsage(tasks, resources);

			Ris = mrsp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp++;

			Ris = fp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifop++;

			Ris = fnp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifonp++;

			Ris = mrsp_mig.NewMrsPRTATest(tasks, resources, MIGRATION_COST, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_mig++;

			Ris = mrsp_np.NewMrsPRTATest(tasks, resources, MIGRATION_COST, 50, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_np++;

			System.out.println(4 + "" + 1 + " " + total_partitions + " times: " + i);

		}

		result += (double) smrsp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) fifop / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) fifonp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) smrsp_np / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) smrsp_mig / (double) TOTAL_NUMBER_OF_SYSTEMS + "\n";
		// System.out.print(result);
		writeSystem(("mig 4 " + 1 + " " + total_partitions), result);
	}

	public static void experimentIncreasingWorkLoad(int bigSet, int smallSet) {
		int NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE = 3;
		double RESOURCE_SHARING_FACTOR = 0.2 + 0.1 * (double) (bigSet - 1);

		int NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION = smallSet;

		SystemGenerator generator = new SystemGenerator(MIN_PERIOD, MAX_PERIOD, 0.1 * (double) NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION,
				TOTAL_PARTITIONS, NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION, true, CS_LENGTH_RANGE.MEDIUM_CS_LEN, RESOURCES_RANGE.HALF_PARITIONS,
				RESOURCE_SHARING_FACTOR, NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE);

		long[][] Ris;
		NewMrsPRTA mrsp = new NewMrsPRTA();
		NewMrsPRTAWithMC mrsp_mig = new NewMrsPRTAWithMC();
		NewMrsPRTAWithMCNP mrsp_np = new NewMrsPRTAWithMCNP();
		FIFOP fp = new FIFOP();
		FIFONP fnp = new FIFONP();

		String result = "";
		int smrsp = 0;
		int smrsp_mig = 0;
		int smrsp_np100 = 0;
		int fifop = 0;
		int fifonp = 0;

		for (int i = 0; i < TOTAL_NUMBER_OF_SYSTEMS; i++) {
			ArrayList<ArrayList<SporadicTask>> tasks = generator.generateTasks();
			ArrayList<Resource> resources = generator.generateResources();
			generator.generateResourceUsage(tasks, resources);

			Ris = mrsp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp++;

			Ris = fp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifop++;

			Ris = fnp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifonp++;

			Ris = mrsp_np.NewMrsPRTATest(tasks, resources, MIGRATION_COST, 100, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_np100++;

			Ris = mrsp_mig.NewMrsPRTATest(tasks, resources, MIGRATION_COST, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_mig++;

			System.out.println(1 + "" + bigSet + " " + smallSet + " times: " + i);

		}

		result += (double) smrsp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) fifonp / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) fifop / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) smrsp_np100 / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) smrsp_mig / (double) TOTAL_NUMBER_OF_SYSTEMS + "\n";

		// System.out.print(result);

		writeSystem(("mig 1 " + bigSet + " " + smallSet), result);
	}

	public static void experimentIncreasingCriticalSectionLength(int bigSet, int smallSet) {
		double RESOURCE_SHARING_FACTOR = 0.4;
		int NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE = 3;
		int NUMBER_OF_TASKS_ON_EACH_PARTITION = 3 + 1 * (bigSet - 1);

		// CS_LENGTH_RANGE range = null;
		// switch (smallSet) {
		// case 1:
		// range = CS_LENGTH_RANGE.VERY_SHORT_CS_LEN;
		// break;
		// case 2:
		// range = CS_LENGTH_RANGE.SHORT_CS_LEN;
		// break;
		// case 3:
		// range = CS_LENGTH_RANGE.MEDIUM_CS_LEN;
		// break;
		// case 4:
		// range = CS_LENGTH_RANGE.LONG_CSLEN;
		// break;
		// case 5:
		// range = CS_LENGTH_RANGE.VERY_LONG_CSLEN;
		// break;
		// default:
		// System.out.println("wrong cs len");
		// break;
		// }

		SystemGenerator generator = new SystemGenerator(MIN_PERIOD, MAX_PERIOD, 0.1 * (double) NUMBER_OF_TASKS_ON_EACH_PARTITION, TOTAL_PARTITIONS,
				NUMBER_OF_TASKS_ON_EACH_PARTITION, true, null, RESOURCES_RANGE.PARTITIONS, RESOURCE_SHARING_FACTOR,
				NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE, smallSet);

		long[][] Ris;
		NewMrsPRTA mrsp = new NewMrsPRTA();
		NewMrsPRTAWithMC mrsp_mig = new NewMrsPRTAWithMC();
		NewMrsPRTAWithMCNP mrsp_np = new NewMrsPRTAWithMCNP();
		FIFOP fp = new FIFOP();
		FIFONP fnp = new FIFONP();

		String result = "";
		int smrsp = 0;
		int smrsp_mig = 0;
		int fifop = 0;
		int fifonp = 0;
		int smrsp_np = 0;

		for (int i = 0; i < TOTAL_NUMBER_OF_SYSTEMS; i++) {
			ArrayList<ArrayList<SporadicTask>> tasks = generator.generateTasks();
			ArrayList<Resource> resources = generator.generateResources();
			generator.generateResourceUsage(tasks, resources);

			Ris = mrsp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp++;

			Ris = fp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifop++;

			Ris = fnp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifonp++;

			Ris = mrsp_mig.NewMrsPRTATest(tasks, resources, MIGRATION_COST, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_mig++;

			Ris = mrsp_np.NewMrsPRTATest(tasks, resources, MIGRATION_COST, smallSet, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_np++;

			System.out.println(1 + "" + bigSet + " " + smallSet + " times: " + i);

		}

		result += (double) smrsp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) fifop / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) fifonp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) smrsp_np / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) smrsp_mig / (double) TOTAL_NUMBER_OF_SYSTEMS + "\n";

		writeSystem(("mig 2 " + bigSet + " " + smallSet), result);
	}

	public static void experimentIncreasingContention(int bigSet, int smallSet) {
		double RESOURCE_SHARING_FACTOR = 0.4;
		int NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE = 1 + 5 * (smallSet - 1);
		int NUMBER_OF_TASKS_ON_EACH_PARTITION = 4;

		SystemGenerator generator = new SystemGenerator(MIN_PERIOD, MAX_PERIOD, 0.1 * (double) NUMBER_OF_TASKS_ON_EACH_PARTITION, TOTAL_PARTITIONS,
				NUMBER_OF_TASKS_ON_EACH_PARTITION, true, CS_LENGTH_RANGE.MEDIUM_CS_LEN, RESOURCES_RANGE.PARTITIONS, RESOURCE_SHARING_FACTOR,
				NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE);

		long[][] Ris;
		NewMrsPRTA mrsp = new NewMrsPRTA();
		NewMrsPRTAWithMC mrsp_mig = new NewMrsPRTAWithMC();
		NewMrsPRTAWithMCNP mrsp_np = new NewMrsPRTAWithMCNP();
		FIFOP fp = new FIFOP();
		FIFONP fnp = new FIFONP();

		String result = "";
		int smrsp = 0;
		int smrsp_mig = 0;
		int fifop = 0;
		int fifonp = 0;
		int smrsp_np = 0;

		for (int i = 0; i < TOTAL_NUMBER_OF_SYSTEMS; i++) {
			ArrayList<ArrayList<SporadicTask>> tasks = generator.generateTasks();
			ArrayList<Resource> resources = generator.generateResources();
			generator.generateResourceUsage(tasks, resources);

			Ris = mrsp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp++;

			Ris = fp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifop++;

			Ris = fnp.NewMrsPRTATest(tasks, resources, false);
			if (isSystemSchedulable(tasks, Ris))
				fifonp++;

			Ris = mrsp_mig.NewMrsPRTATest(tasks, resources, MIGRATION_COST, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_mig++;

			Ris = mrsp_np.NewMrsPRTATest(tasks, resources, MIGRATION_COST, 100, false);
			if (isSystemSchedulable(tasks, Ris))
				smrsp_np++;

			System.out.println(1 + "" + bigSet + " " + smallSet + " times: " + i);

		}

		result += (double) smrsp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) fifop / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) fifonp / (double) TOTAL_NUMBER_OF_SYSTEMS + " " + (double) smrsp_np / (double) TOTAL_NUMBER_OF_SYSTEMS + " "
				+ (double) smrsp_mig / (double) TOTAL_NUMBER_OF_SYSTEMS + "\n";
		// System.out.print(result);
		writeSystem(("mig 3 " + bigSet + " " + smallSet), result);
	}

	public static boolean isSystemSchedulable(ArrayList<ArrayList<SporadicTask>> tasks, long[][] Ris) {
		for (int i = 0; i < tasks.size(); i++) {
			for (int j = 0; j < tasks.get(i).size(); j++) {
				if (tasks.get(i).get(j).deadline < Ris[i][j])
					return false;
			}
		}
		return true;
	}

	public static void writeSystem(String filename, String result) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new FileWriter(new File("result/" + filename + ".txt"), false));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		writer.println(result);
		writer.close();
	}
}
