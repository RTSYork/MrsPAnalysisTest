package test;

import java.util.ArrayList;

import basicAnalysis.NewMrsPRTAWithMC;
import basicAnalysis.NewMrsPRTAWithMCNP;
import entity.Resource;
import entity.SporadicTask;
import generatorTools.SystemGenerator;
import generatorTools.SystemGenerator.CS_LENGTH_RANGE;
import generatorTools.SystemGenerator.RESOURCES_RANGE;

public class MigCostTest {

	public static int TOTAL_NUMBER_OF_SYSTEMS = 99999999;
	public static int TOTAL_PARTITIONS = 8;
	public static int MIN_PERIOD = 1;
	public static int MAX_PERIOD = 1000;
	public static int NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION = 16;
	public static int NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE = 5;
	public static double RESOURCE_SHARING_FACTOR = .5;

	public static void main(String[] args) {

		NewMrsPRTAWithMC mrsp_mc = new NewMrsPRTAWithMC();
		NewMrsPRTAWithMCNP mrsp_mcnp = new NewMrsPRTAWithMCNP();

		SystemGenerator generator = new SystemGenerator(MIN_PERIOD, MAX_PERIOD, 0.1 * (double) NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION,
				TOTAL_PARTITIONS, NUMBER_OF_MAX_TASKS_ON_EACH_PARTITION, true, CS_LENGTH_RANGE.MEDIUM_CS_LEN, RESOURCES_RANGE.PARTITIONS,
				RESOURCE_SHARING_FACTOR, NUMBER_OF_MAX_ACCESS_TO_ONE_RESOURCE);

		for (int i = 0; i < 1000000; i++) {
			ArrayList<ArrayList<SporadicTask>> tasks = generator.generateTasks();
			ArrayList<Resource> resources = generator.generateResources();
			generator.generateResourceUsage(tasks, resources);
			System.out.println(i);

			mrsp_mcnp.NewMrsPRTATest(tasks, resources, 1, 2, false);

			mrsp_mc.NewMrsPRTATest(tasks, resources, 1, false);

		}

	}

	public static boolean isEqual(long[][] r1, long[][] r2) {
		for (int i = 0; i < r1.length; i++) {
			for (int j = 0; j < r1[i].length; j++) {
				if (r1[i][j] != r2[i][j]) {
					System.out.println("not equal at:  i=" + i + "  j=" + j + "   r1: " + r1[i][j] + "   r2:" + r2[i][j]);

					return false;
				}
			}
		}
		return true;
	}

}
