package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

import com.google.common.collect.FluentIterable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates random data set based on defined profiles.
 *
 * @author Aditya Bhardwaj
 */
public class MatchingDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingDataGenerator.class);

    public static final Gson GSON = new Gson();
    public static final Type TRANSACTION_LIST_TYPE = new TypeToken<List<Transaction>>() {
    }.getType();
    public static final Scenario SCENARIO_LOW_VOLUME = new Scenario(10, 5, 10, 20, 50);
    public static final Scenario SCENARIO_MID_VOLUME = new Scenario(100, 5, 10, 20, 50);
    public static final Scenario SCENARIO_HIGH_VOLUME = new Scenario(1000, 5, 10, 20, 50);
    public static final Scenario SCENARIO_VHIGH_VOLUME = new Scenario(10000, 5, 10, 20, 50);
    public static final Random RND_SEED = new Random(1343254354653L);


    //positive and realistic scenarios

    //10, 100, 1000 txns
    //price variation within 5% to 10%, 50%, 1000%
    //quantity between 10 to 20
    //select random set of 20%, 50%, 70%, 100% trades

    //unrealistic scenarios

    //Add spike to the data set i.e. 2% of spike
    //select with and without spike

    //impossible scenarios

    @Test
    public void generateData() throws IOException {
        boolean doNotOverwrite = true;
        generateAndWriteData(SCENARIO_LOW_VOLUME, doNotOverwrite);
        generateAndWriteData(SCENARIO_MID_VOLUME, doNotOverwrite);
        generateAndWriteData(SCENARIO_HIGH_VOLUME, doNotOverwrite);
        generateAndWriteData(SCENARIO_VHIGH_VOLUME, doNotOverwrite);


    }

    private ProblemStatement generateAtomicProblem(final List<Transaction> txns, int percentage) {
        //randomise collection and select x% of trades
        List<Transaction> cloneTxns = new ArrayList<Transaction>(txns);
        Collections.shuffle(cloneTxns, RND_SEED);
        int nElements = (cloneTxns.size() * percentage) / 100;

        List<Transaction> solutionTxns = FluentIterable.from(cloneTxns).limit(nElements).toList();
        Transaction problemTxn = new Transaction(ListFn.agg(solutionTxns, ListFn.COST_FN), ListFn.agg(solutionTxns, ListFn.QUANTITY_FN));
        return new ProblemStatement(txns, problemTxn, solutionTxns);
    }

    private void generateComplexProblem(List<Transaction> txns) {
        //randomise collection
        //Select x number of complete transactions

    }

    private void generateAndWriteData(Scenario scenario, boolean doNotOverwrite) throws IOException {
        File file = new File(scenario.fileName());
        if(file.exists() && doNotOverwrite) {
            LOGGER.warn("Skipping {}", scenario.fileName());
            return;
        }
        List<Transaction> txns = generate(scenario);
        String data = serialise(txns);
        FileUtils.writeStringToFile(file, data);
        LOGGER.info("Generated data \n {} \n {}", data, file.getAbsolutePath());
    }

    private List<Transaction> generate(Scenario scenario) {
        BigDecimal price = BigDecimal.valueOf(scenario.getStartPrice());

        List<Transaction> sampleTxns = new ArrayList<Transaction>();
        for(int i=0; i< scenario.getTotalTxns(); i++) {
            long quantity = Randomiser.rangeL(scenario.getLowQuantity(), scenario.getHighQuantity());
            price = Randomiser.variation(price, scenario.getPriceVariationInPercentage()).setScale(6, RoundingMode.HALF_DOWN);
            sampleTxns.add(new Transaction(quantity, price));
        }

        return sampleTxns;
    }

    private String serialise(List<Transaction> txns) {
        return GSON.toJson(txns, TRANSACTION_LIST_TYPE);
    }

    private List<Transaction> deserialise(String serialTxns) {
        return GSON.fromJson(serialTxns, TRANSACTION_LIST_TYPE);
    }
}
