/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved.
 * Proprietary, do not copy or distribute without permission.
 * Written by Jessa Bekker and Pieter Robberechts, 2020
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * This class is a stub for a perceptron with count-min sketch
 */
public class PerceptronFeatureHashing extends OnlineTextClassifier{
    private int nbOfBuckets;
    private int logNbOfBuckets;
    private double learningRate;
    private double bias;
    private double[] weights; //weights[i]: The weight for n-grams that hash to value i
    /* FILL IN HERE */

    /**
     * Initialize the perceptron classifier
     *
     * THIS CONSTRUCTOR IS REQUIRED, DO NOT CHANGE THE HEADER
     * You can write additional constructors if you wish, but make sure this one works
     *
     * This classifier uses simple feature hashing: The features of this classifier are the hash values that n-grams
     * hash to.
     *
     * @param logNbOfBuckets The hash functions hash to the range [0,2^NbOfBuckets-1]
     * @param learningRate The size of the updates of the weights
     */
    public PerceptronFeatureHashing(int logNbOfBuckets, double learningRate){
        this.logNbOfBuckets=logNbOfBuckets;
        this.learningRate = learningRate;
        // this.learningRate = 0.7;
        this.nbOfBuckets=((int) Math.pow(2, logNbOfBuckets)-1);
        // this.bias = 0;
        this.weights = new double[this.nbOfBuckets];

        //
        this.threshold = 0.5;
        this.bias = Math.random();
        for (int i=0; i<this.nbOfBuckets; i++){
            this.weights[i] = Math.random();
        }
    }


    /**
     * Calculate the hash value for string str
     *
     * THIS METHOD IS REQUIRED
     *
     * The hash function hashes to the range [0,2^NbOfBuckets-1]
     *
     * @param str The string to calculate the hash function for
     * @return the hash value of the h'th hash function for string str
     */
    private int hash(String str){
        int hash32 = MurmurHash.hash32(str, 2020);
        int hashValue =  (Math.abs(hash32)) % this.nbOfBuckets;
        return hashValue;
    }

    /**
     * This method will update the parameters of your model using the incoming mail.
     *
     * THIS METHOD IS REQUIRED
     *
     * @param labeledText is an incoming e-mail with a spam/ham label
     */
    @Override
    public void update(LabeledText labeledText){
        super.update(labeledText);
        /* FILL IN HERE */
        double prediction = this.makePrediction(labeledText.text);
        int pr = this.classify(prediction);
        this.bias = this.bias + this.learningRate * (labeledText.label - pr);
        for (String ngram: labeledText.text.ngrams){
            int hashValue = hash(ngram);
            weights[hashValue] = weights[hashValue] + this.learningRate * (labeledText.label - pr);
        }
    }


    /**
     * Uses the current model to make a prediction about the incoming e-mail belonging to class "1" (spam)
     * If the prediction is positive, then the e-mail is classified as spam.
     *
     * This method gives the output of the perceptron, before it is passed through the threshold function.
     *
     * THIS METHOD IS REQUIRED
     *
     * @param text is an parsed incoming e-mail
     * @return the prediction
     */
    @Override
    public double makePrediction(ParsedText text) {
        double pr = 0.0;
        for (String ngram: text.ngrams){
            int hashValue = hash(ngram);
            pr = pr + hashValue*weights[hashValue];
        }
        return pr+bias;
    }


    /**
     * This runs your code.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 7) {
            System.err.println("Usage: java PerceptronFeatureHashing <indexPath> <stopWordsPath> <logNbOfBuckets> <learningRate> <outPath> <reportingPeriod> <maxN> [-writeOutAllPredictions]");
            throw new Error("Expected 7 or 8 arguments, got " + args.length + ".");
        }
        try {
            // parse input
            String indexPath = args[0];
            String stopWordsPath = args[1];
            int logNbOfBuckets = Integer.parseInt(args[2]);
            double learningRate = Double.parseDouble(args[3]);
            String out = args[4];
            int reportingPeriod = Integer.parseInt(args[5]);
            int n = Integer.parseInt(args[6]);
            boolean writeOutAllPredictions = args.length>7 && args[7].equals("-writeOutAllPredictions");

            // initialize e-mail stream
            MailStream stream = new MailStream(indexPath, new EmlParser(stopWordsPath,n));

            // initialize learner
            PerceptronFeatureHashing perceptron = new PerceptronFeatureHashing(logNbOfBuckets, learningRate);

            // generate output for the learning curve
            EvaluationMetric[] evaluationMetrics = new EvaluationMetric[]{new Accuracy(), new Precision(),
                    new Recall(), new Fmeasure() }; // ADD AT LEAST TWO MORE EVALUATION METRICS
            perceptron.makeLearningCurve(stream, evaluationMetrics, out+".pfh", reportingPeriod, writeOutAllPredictions);

        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
    }


}
