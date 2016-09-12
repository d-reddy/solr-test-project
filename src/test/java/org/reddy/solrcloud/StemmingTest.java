package org.reddy.solrcloud;

import org.reddy.datacontracts.Properties;
import org.reddy.helpers.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dreddy on 8/25/2016.
 */
public class StemmingTest {

    @Test
    public void whenTokenStemmedOnQuery_canBeRetrievedWithSearch() throws Exception {
        Properties properties = PropertyConfig.getTestProperties("stemming/test1.json");
        ArrayList<Integer> searchDocResults = Solr.index_search_cleanup(properties);

        //verify
        Assert.assertTrue(searchDocResults.size() > 0);
        Assert.assertTrue(Arrays.equals(searchDocResults.toArray(),properties.search.expectedResult.toArray()));

    }

    @Test
    public void whenMultipleDocsWithVariationofStemmedSearchTerm_hasExactSearchTermsOrderedFirst() throws Exception {
        Properties properties = PropertyConfig.getTestProperties("stemming/test2.json");
        ArrayList<Integer> searchDocResults = Solr.index_search_cleanup(properties);

        //verify
        Assert.assertTrue(searchDocResults.size() > 0);
        Assert.assertTrue(searchDocResults.subList(0,2).containsAll(properties.search.expectedResult));
    }
}
