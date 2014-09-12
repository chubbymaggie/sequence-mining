package itemsetmining.transaction;

import itemsetmining.itemset.Itemset;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.io.LineIterator;
import org.apache.commons.math3.distribution.LogNormalDistribution;

import com.google.common.collect.Maps;

public class TransactionGenerator {

	private static final boolean VERBOSE = false;

	/**
	 * Create interesting itemsets that highlight problems
	 * 
	 * @param difficultyLevel
	 *            An integer between 0 and 10
	 * 
	 * @param noInstances
	 *            The number of example itemset instances
	 */
	public static HashMap<Itemset, Double> generateExampleItemsets(
			final String name, final int noInstances, final int difficultyLevel) {

		final HashMap<Itemset, Double> itemsets = Maps.newHashMap();

		// Difficulty scaling (times 10^0 to 10^-1)
		final double scaling = Math.pow(10, -difficultyLevel / 10.);

		int maxElement = 0;
		for (int j = 0; j < noInstances; j++) {

			// Here [1 2] is the champagne & caviar problem
			// (not generated when support is too high)
			if (name.equals("caviar")) {

				// Champagne & Caviar
				final Itemset s12 = new Itemset(maxElement + 1, maxElement + 2);
				final double p12 = 0.1 * scaling;
				itemsets.put(s12, p12);
				maxElement += 2;

			}
			// Here [1 2 3] would be seen as a frequent itemset
			// as both [1 2] and [3] are frequent
			else if (name.equals("freerider")) {

				final Itemset s12 = new Itemset(maxElement + 1, maxElement + 2);
				final Itemset s3 = new Itemset(maxElement + 3);
				final double p12 = 0.5 * scaling;
				final double p3 = 0.5 * scaling;
				itemsets.put(s12, p12);
				itemsets.put(s3, p3);
				maxElement += 3;

			}
			// Here [1 2 3] is known as a cross-support pattern
			// (spuriously generated when support is too low)
			else if (name.equals("cross-supp")) {

				final Itemset s1 = new Itemset(maxElement + 1);
				final double p1 = 0.95 * scaling;
				itemsets.put(s1, p1);

				final Itemset s2 = new Itemset(maxElement + 2, maxElement + 3);
				final double p2 = 0.2 * scaling;
				itemsets.put(s2, p2);
				maxElement += 3;

			} else
				throw new IllegalArgumentException("Incorrect problem name.");

		}

		return itemsets;
	}

	/** Generate some disjoint itemsets as background noise */
	public static HashMap<Itemset, Double> getNoisyItemsets(
			final int noItemsets, final double mu, final double sigma) {

		final HashMap<Itemset, Double> noisyItemsets = Maps.newHashMap();

		final LogNormalDistribution dist = new LogNormalDistribution(mu, sigma);
		final Random rand = new Random(1);

		int maxElement = 20;
		for (int j = 0; j < noItemsets; j++) {

			final int len = (int) Math.round(dist.sample());
			final Itemset set = new Itemset();
			for (int i = maxElement; i < maxElement + len; i++) {
				set.add(i);
			}
			final int num = rand.nextInt(7) + 2;
			noisyItemsets.put(set, num / 10.);
			maxElement += len;

		}

		return noisyItemsets;
	}

	/** Generate transactions from set of interesting itemsets */
	public static void generateTransactionDatabase(
			final HashMap<Itemset, Double> itemsets, final int noTransactions,
			final File outFile) throws IOException {

		// Set output file
		final PrintWriter out = new PrintWriter(outFile, "UTF-8");

		// Generate transaction database
		for (int i = 0; i < noTransactions; i++) {

			// Generate transaction from distribution
			final Transaction transaction = sampleFromDistribution(itemsets);
			for (final int item : transaction) {
				out.print(item + " ");
			}
			if (!transaction.isEmpty())
				out.println();

		}
		out.close();

		// Print file to screen
		if (VERBOSE) {
			final FileReader reader = new FileReader(outFile);
			final LineIterator it = new LineIterator(reader);
			while (it.hasNext()) {
				System.out.println(it.nextLine());
			}
			LineIterator.closeQuietly(it);
		}
	}

	/** Randomly generate itemset with its probability */
	private static Transaction sampleFromDistribution(
			final HashMap<Itemset, Double> itemsets) {

		final Transaction transaction = new Transaction();
		for (final Entry<Itemset, Double> entry : itemsets.entrySet()) {
			if (Math.random() < entry.getValue()) {
				transaction.add(entry.getKey());
			}
		}

		return transaction;
	}
}
