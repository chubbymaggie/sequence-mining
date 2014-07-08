package itemsetmining.transaction;

import static org.junit.Assert.assertTrue;
import itemsetmining.itemset.Itemset;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.google.common.collect.Sets;

public class TransactionTest {

	@Test
	public void testNoisyItemsets() {

		final HashMap<Itemset, Double> itemsets = TransactionGenerator
				.getNoisyItemsets(10, 3);
		System.out.println(itemsets);

		// Check that all items >= 10
		for (final Itemset set : itemsets.keySet()) {
			for (final int item : set) {
				assertTrue(item >= 10);
			}
		}

		// Check that disjoint
		for (final Itemset set1 : itemsets.keySet()) {
			final HashSet<Itemset> setItemset = new HashSet<Itemset>();
			setItemset.add(set1);
			for (final Itemset set2 : Sets.difference(itemsets.keySet(),
					setItemset)) {
				assertTrue(Collections.disjoint(set1, set2));
			}
		}

	}

}
