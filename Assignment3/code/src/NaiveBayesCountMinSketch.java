
/**
 * Copyright (c) DTAI - KU Leuven – All rights reserved.
 * Proprietary, do not copy or distribute without permission.
 * Written by Jessa Bekker and Pieter Robberechts, 2020
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;


/**
 * This class is a stub for naive Bayes with count-min sketch
 */
public class NaiveBayesCountMinSketch extends OnlineTextClassifier{
    private int nbOfBuckets;
    private int nbOfHashes;
    private int logNbOfBuckets;
    private int[][][] counts; // counts[c][h][i]: The count of n-grams in e-mails of class c (spam: c=1)
                              // that hash to value i for the h'th hash function.
    private int[] classCounts; //classCounts[c] the count of e-mails of class c (spam: c=1)

    /* FILL IN HERE */
    private Function<String, Integer>[] hashFunctions;
    /**
     * Initialize the naive Bayes classifier
     *
     * THIS CONSTRUCTOR IS REQUIRED, DO NOT CHANGE THE HEADER
     * You can write additional constructors if you wish, but make sure this one works
     *
     * This classifier uses the count-min sketch to estimate the conditional counts of the n-grams
     *
     * @param nbOfHashes The number of hash functions in the count-min sketch
     * @param logNbOfBuckets The hash functions hash to the range [0,2^NbOfBuckets-1]
     * @param threshold The threshold for classifying something as positive (spam). Classify as spam if Pr(Spam|n-grams)>threshold)
     */
    public NaiveBayesCountMinSketch(int nbOfHashes, int logNbOfBuckets, double threshold){
        this.nbOfHashes = nbOfHashes;
        this.logNbOfBuckets=logNbOfBuckets;
        this.threshold = threshold;

        /* FILL IN HERE */

        this.nbOfBuckets = (int) Math.pow(2, logNbOfBuckets) - 1;
        this.counts = new int[2][nbOfHashes][this.nbOfBuckets];
        this.classCounts = new int[2];
        this.hashFunctions = new Function[nbOfHashes];
        for (int i = 0; i < nbOfHashes; i++) {
            // int randomInt = (int)Math.random()*NbOfBuckets;
            this.hashFunctions[i] = newHashFunction(i);
        }

    }

    private Function<String, Integer> newHashFunction(int seed) {
        return str -> (Math.abs(MurmurHash.hash32(str, seed)) % this.nbOfBuckets);
    }

    /**
     * Calculate the hash value of the h'th hash function for string str
     *
     * THIS METHOD IS REQUIRED
     *
     * The hash function hashes to the range [0,2^NbOfBuckets-1] This method should
     * work for h in the range [0, nbOfHashes-1]
     *
     * @param str The string to calculate the hash function for
     * @param h   The number of the hash function to use.
     * @return the hash value of the h'th hash function for string str
     */
    private int hash(String str, int h) {

        /* FILL IN HERE */
        return hashFunctions[h].apply(str);
    }

    /**
     * This method will update the parameters of your model using the incoming mail.
     *
     * THIS METHOD IS REQUIRED
     *
     * @param labeledText is an incoming e-mail with a spam/ham label
     */
    @Override
    public void update(LabeledText labeledText) {
        super.update(labeledText);

        /* FILL IN HERE */
        this.classCounts[labeledText.label]++;
        for (int i = 0; i < this.nbOfHashes; i++) {
            Set<Integer> ngramsHashSet = new HashSet<>();
            int hashcode;
            for (String ngram : labeledText.text.ngrams) {
                hashcode = hash(ngram, i);
                ngramsHashSet.add(hashcode);
            }
            for (int j : ngramsHashSet) {
                counts[labeledText.label][i][j]++;
            }
        }
    }

    /**
     * Uses the current model to make a prediction about the incoming e-mail
     * belonging to class "1" (spam) The prediction is the probability for the
     * e-mail to be spam. If the probability is larger than the threshold, then the
     * e-mail is classified as spam.
     *
     * THIS METHOD IS REQUIRED
     *
     * @param text is an parsed incoming e-mail
     * @return the prediction
     */
    @Override
    public double makePrediction(ParsedText text) {
        double pr;
        // computes the joint probability of each class and all feature
        double prHam = (double) classCounts[0] / this.nbExamplesProcessed;
        double prSpam = 1 - prHam;
        double prWordGivenSpam = 0;
        double prWordGivenHam = 0;
        // for spam
        int[] minCountWord4Ham = new int[text.ngrams.size()];
        int[] minCountWord4Spam = new int[text.ngrams.size()];
        for (int i = 0; i < text.ngrams.size(); i++) {
            minCountWord4Spam[i] = Integer.MAX_VALUE;
            minCountWord4Ham[i] = Integer.MAX_VALUE;
        }
        for (int hashFunctions = 0; hashFunctions < this.nbOfHashes; hashFunctions++) {
            int i = 0;
            for (String ngram : text.ngrams) {
                int hashedNgram = this.hashFunctions[hashFunctions].apply(ngram);
                int count = this.counts[1][hashFunctions][hashedNgram];
                if (count < minCountWord4Spam[i]) {
                    minCountWord4Spam[i] = count;
                }
                i++;
            }
        }
        for (int hashFunctions = 0; hashFunctions < this.nbOfHashes; hashFunctions++) {
            int i = 0;
            for (String ngram : text.ngrams) {
                int hashedNgram = this.hashFunctions[hashFunctions].apply(ngram);
                int count = this.counts[0][hashFunctions][hashedNgram];
                if (count < minCountWord4Ham[i]) {
                    minCountWord4Ham[i] = count;
                }
                i++;
            }
        }
        for (int i = 0; i < text.ngrams.size(); i++) {
            prWordGivenSpam += Math.log((1.0 + minCountWord4Spam[i]) / (this.classCounts[1] + this.nbOfBuckets));
            prWordGivenHam += Math.log((1.0 + minCountWord4Ham[i]) / (this.classCounts[0] + this.nbOfBuckets));
        }

        double a = Math.log(prHam)+prWordGivenHam;
        double b = Math.log(prSpam)+prWordGivenSpam;
        double prText = a + Math.log(1+Math.exp(b-a));
        pr = Math.exp(b-prText);
        return pr;

    }


    /**
     * This runs your code.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 8) {
            System.err.println("Usage: java NaiveBayesCountMinSketch <indexPath> <stopWordsPath> <logNbOfBuckets> <nbOfHashes> <threshold> <outPath> <reportingPeriod> <maxN> [-writeOutAllPredictions]");
            throw new Error("Expected 8 or 9 arguments, got " + args.length + ".");
        }
        try {
            // parse input
            String indexPath = args[0];
            String stopWordsPath = args[1];
            int logNbOfBuckets = Integer.parseInt(args[2]);
            int nbOfHashes = Integer.parseInt(args[3]);
            double threshold = Double.parseDouble(args[4]);
            String out = args[5];
            int reportingPeriod = Integer.parseInt(args[6]);
            int n = Integer.parseInt(args[7]);
            boolean writeOutAllPredictions = args.length>8 && args[8].equals("-writeOutAllPredictions");

            // initialize e-mail stream
            MailStream stream = new MailStream(indexPath, new EmlParser(stopWordsPath,n));

            // initialize learner
            NaiveBayesCountMinSketch nb = new NaiveBayesCountMinSketch(nbOfHashes ,logNbOfBuckets, threshold);

            // generate output for the learning curve
            EvaluationMetric[] evaluationMetrics = new EvaluationMetric[]{new Accuracy(), new Precision(), new Recall(), new Fmeasure()}; //ADD AT LEAST TWO MORE EVALUATION METRICS
            nb.makeLearningCurve(stream, evaluationMetrics, out+".nbcms", reportingPeriod, writeOutAllPredictions);

        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
    }
}
