package com.knaptus.coding.handson.npcomplete.optaplanner.matcher;

public class Scenario {
    private final int totalTxns;
    private final double priceVariationInPercentage;
    private final long lowQuantity;
    private final long highQuantity;
    private final double startPrice;

    public Scenario(int totalTxns, double priceVariationInPercentage, long lowQuantity, long highQuantity, double startPrice) {
        this.totalTxns = totalTxns;
        this.priceVariationInPercentage = priceVariationInPercentage;
        this.lowQuantity = lowQuantity;
        this.highQuantity = highQuantity;
        this.startPrice = startPrice;
    }

    public int getTotalTxns() {
        return totalTxns;
    }

    public double getPriceVariationInPercentage() {
        return priceVariationInPercentage;
    }

    public long getLowQuantity() {
        return lowQuantity;
    }

    public long getHighQuantity() {
        return highQuantity;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public String fileName() {
        return String.format("src/test/resources/scenariodata/%d_txns_%.0fvar_%dto%dlots_%.0fprice.json",
                getTotalTxns(),
                getPriceVariationInPercentage(),
                getLowQuantity(), getHighQuantity(),
                getStartPrice());
    }
}
