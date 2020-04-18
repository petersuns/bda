/**
 * auther@Shuo Sun 
 */


/**
 * This class calculates the Precision
 */
public class Precision implements EvaluationMetric {

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
        if (TP+FP>0){
            return ((double) TP)/(TP+FP);
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
        return "precision";
    }
}
