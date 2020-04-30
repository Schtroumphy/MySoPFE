package fr.eseo.hervy.sopfe.Manager;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created on 11/10/2019 - 17:39.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : InputFilterMinMax
 */
public class InputFilterMinMax implements InputFilter {

    private int min, max;

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try{
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        }catch (NumberFormatException nfe) {
        }
        return "";
    }

    private boolean isInRange(int min, int max, int input) {
        return max > min ? input >= min && input <= max : input <= min;
    }

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }



}
