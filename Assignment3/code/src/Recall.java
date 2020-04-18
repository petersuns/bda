/**
 * auther@Shuo Sun 
 */


/**
 * This class calculates the recall
 */
public class Recall implements EvaluationMetric {

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
        if (TP+FN>0){
            return ((double) TP)/(TP+FN);
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
        return "recall";
    }
}
