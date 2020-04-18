/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved.
 * Proprietary, do not copy or distribute without permission.
 * Written by Jessa Bekker and Pieter Robberechts, 2020
 */


/**
 * This class calculates the Fmeasure
 */
public class Fmeasure implements EvaluationMetric {

    /**
     * Calculates the accuracy given the values of the contingency table
     *
     * @param TP Number of true positives
     * @param FP Number of false positives
     * @param TN Number of true negatives
     * @param FN Number of false negatives
     * @return evaluation evaluate
     */
    @Override
    public double evaluate(int TP, int FP, int TN, int FN) {
        if (TP > 0 || FP>0 || TP>0){
            double recall = ((double) TP)/(TP+FN);
            double precision =  ((double) TP)/(TP+FP);
            return 2*recall*precision/(recall+precision);
        }
        else{
            return 0.0;
        }
    }

    /**
     *
     * @return name of the evaluator
     */
    @Override
    public String name() {
        return "Fmeasure";
    }
}
