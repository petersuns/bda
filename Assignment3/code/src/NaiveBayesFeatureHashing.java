
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
 * This class is a stub for naive bayes with feature hashing
 */
public class NaiveBayesFeatureHashing extends OnlineTextClassifier{
    public int nbOfBuckets;
    public int logNbOfBuckets;
    public int[][] counts; // counts[c][i]: The count of n-grams in e-mails of class c (spam: c=1) that hash to value i
    public int[] classCounts; //classCounts[c] the count of e-mails of class c (spam: c=1)
    /* FILL IN HERE */

    /**
     * Initialize the naive Bayes classifier
     *
     * THIS CONSTRUCTOR IS REQUIRED, DO NOT CHANGE THE HEADER
     * You can write additional constructors if you wish, but make sure this one works
     *
     * This classifier uses simple feature hashing: The features of this classifier are the hash values that n-grams
     * hash to.
     *
     * @param logNbOfBuckets The hash function hashes to the range [0,2^NbOfBuckets-1]
     * @param threshold The threshold for classifying something as positive (spam). Classify as spam if Pr(Spam|n-grams)>threshold)
     */
    public NaiveBayesFeatureHashing(int logNbOfBuckets, double threshold){
        this.logNbOfBuckets=logNbOfBuckets;
        this.threshold = threshold;

        /* FILL IN HERE */
        this.nbOfBuckets=((int) Math.pow(2, logNbOfBuckets)-1);
        this.classCounts = new int[2];
        this.counts = new int[2][this.nbOfBuckets];

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
    public int hash(String str){
        int hashcode = MurmurHash.hash32(str, 2020);
        return (hashcode & 0x7FFFFFFF) % this.nbOfBuckets;//???????
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
        this.classCounts[labeledText.label]++;
        Set<Integer> ngramsHashSet = new HashSet <>();
        int hashcode;
        for (String ngram: labeledText.text.ngrams){
            hashcode = hash(ngram);
            ngramsHashSet.add(hashcode);
        }

        for(int ngram: ngramsHashSet){
            counts[labeledText.label][ngram]++ ;
        }
    }


    /**
     * Uses the current model to make a prediction about the incoming e-mail belonging to class "1" (spam)
     * The prediction is the probability for the e-mail to be spam.
     * If the probability is larger than the threshold, then the e-mail is classified as spam.
     *
     * THIS METHOD IS REQUIRED
     *
     * @param text is an parsed incoming e-mail
     * @return the prediction
     */
    @Override
    public double makePrediction(ParsedText text) {
        double pr = 0;
        /* FILL IN HERE */
        Set<Integer> wordSet = new HashSet <>();
        int hashcode;
        for (String ngram: text.ngrams){
            hashcode = hash(ngram);
            wordSet.add(hashcode);
        }
        double prHam = (double) classCounts[0]/this.nbExamplesProcessed;
        double prSpam = 1 - prHam;
        double prWordGivenSpam=0;
        double prWordGivenHam=0;
        for (int word: wordSet){
            prWordGivenSpam += Math.log((1.0 + counts[1][word]) / (classCounts[1] + this.nbOfBuckets));
            prWordGivenHam += Math.log((1.0 + counts[0][word]) / (classCounts[0] + this.nbOfBuckets));
        }
        double a = Math.log(prHam)+prWordGivenHam;
        double b = Math.log(prSpam)+prWordGivenSpam;
        double prText = a + Math.log(1+Math.exp(b-a));
        // System.out.println(a);
        pr = Math.exp(b-prText);
        return pr;
    }



    /**
     * This runs your code.
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 7) {
            System.err.println("Usage: java NaiveBayesFeatureHashing <indexPath> <stopWordsPath> <logNbOfBuckets> <threshold> <outPath> <reportingPeriod> <maxN> [-writeOutAllPredictions]");
            throw new Error("Expected 7 or 8 arguments, got " + args.length + ".");
        }
        try {
            // parse input
            String indexPath = args[0];
            String stopWordsPath = args[1];
            int logNbOfBuckets = Integer.parseInt(args[2]);
            double threshold = Double.parseDouble(args[3]);
            String out = args[4];
            int reportingPeriod = Integer.parseInt(args[5]);
            int n = Integer.parseInt(args[6]);
            boolean writeOutAllPredictions = args.length>7 && args[7].equals("-writeOutAllPredictions");

            // initialize e-mail stream
            MailStream stream = new MailStream(indexPath, new EmlParser(stopWordsPath,n));

            // initialize learner
            NaiveBayesFeatureHashing nb = new NaiveBayesFeatureHashing(logNbOfBuckets, threshold);

            // generate output for the learning curve
            EvaluationMetric[] evaluationMetrics = new EvaluationMetric[]{new Accuracy(), new Precision(), new Recall(), new Fmeasure()}; //ADD AT LEAST TWO MORE EVALUATION METRICS
            nb.makeLearningCurve(stream, evaluationMetrics, out+".nbfh", reportingPeriod, writeOutAllPredictions);

        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
    }


}
