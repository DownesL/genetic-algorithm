package domain;

public class MinMaxTuple {
    public Integer min;
    public Integer max;

    public MinMaxTuple(Integer min, Integer max) {
        this.min = min;
        this.max = max;
    }

    public MinMaxTuple(String str) {
        // string must be of format: (min,max)
        String pattern = "^\\(\\d+,\\d+\\)$";
        if (!str.matches(pattern)) {
            throw new IllegalArgumentException();
        }
        int commaIndex = str.indexOf(',');
        this.min = Integer.parseInt(str.substring(1,commaIndex));
        this.max = Integer.parseInt(str.substring(commaIndex + 1,str.length()-1));
    }

}
