package sheva.singapp.utils;

/**
 * Created by shevc on 23.07.2017.
 * Let's GO!
 */

public class NotesResolver {

    /// TODO це зробити НОРМАЛЬНО
    /*public static String resolveNoteForFrequency(int freq) {
        int counter = 0;
        while (freq >= 31) {
            freq = freq / 2;
            counter++;
        }

        if (freq <= 31 && freq > 30) {
            return "B" + counter;
        } else if (freq <= 30 && freq > 29) {
            return "A#" + counter;
        } else if (freq <= 29 && freq > 28) {
            return "A" + counter;
        } else if (freq <= 28 && freq > 26) {
            return "G#" + counter;
        } else if (freq <= 26 && freq > 25) {
            return "G" + counter;
        } else if (freq <= 25 && freq > 23) {
            return "F#" + counter;
        } else if (freq <= 23 && freq > 22) {
            return "F" + counter;
        } else if (freq <= 22 && freq > 21) {
            return "E" + counter;
        } else if (freq <= 21 && freq > 19) {
            return "D#" + counter;
        } else if (freq <= 19 && freq > 18) {
            return "D" + counter;
        } else if (freq <= 18 && freq > 17) {
            return "C#" + counter;
        } else if (freq <= 17 && freq > 16) {
            return "C" + counter;
        } else {
            return "";
        }
    }*/

    public static String recognizeNoteByFrequency(int frequency) {
        /// 2 - мала октава
        if (frequency > 126 && frequency < 133) { /// 131
            return "C2";
        }
        else if (frequency > 133 && frequency < 142) { /// 138
            return "C#2";
        }
        else if (frequency > 142 && frequency < 150) { /// 147
            return "D2";
        }
        else if (frequency > 150 && frequency < 160) { /// 155
            return "D#2";
        }
        else if (frequency > 160 && frequency < 169) { /// 165
            return "E2";
        }
        else if (frequency > 169 && frequency < 180) { /// 174
            return "F2";
        }
        else if (frequency > 180 && frequency < 191) { /// 185
            return "F#2";
        }
        else if (frequency > 191 && frequency < 202) { /// 196
            return "G2";
        }
        else if (frequency > 202 && frequency < 215) { /// 207
            return "G#2";
        }
        else if (frequency > 215 && frequency < 228) { /// 220
            return "A2";
        }
        else if (frequency > 228 && frequency < 238) { /// 233
            return "A#2";
        }
        else if (frequency > 238 && frequency < 256) { /// 247
            return "B2";
        }
        else if (frequency > 256 && frequency < 272) { /// 261
            return "C3";
        }
        else if (frequency > 272 && frequency < 288) { /// 277
            return "C#3";
        }
        else if (frequency > 288 && frequency < 306) { /// 293
            return "D3";
        }
        else if (frequency > 306 && frequency < 325) { /// 311
            return "D#3";
        }
        else if (frequency > 325 && frequency < 344) { /// 329 опорно нота - Мі Першої Октави
            return "E3";
        }
        else if (frequency > 344 && frequency < 365) { /// 349
            return "F3";
        }
        else if (frequency > 365 && frequency < 387) { /// 370
            return "F#3";
        }
        else if (frequency > 387 && frequency < 410) { /// 392
            return "G3";
        }
        else if (frequency > 410 && frequency < 435) { /// 415
            return "G#3";
        }
        else if (frequency > 435 && frequency < 461) { /// 440
            return "A3";
        }
        else if (frequency > 461 && frequency < 488) { /// 466
            return "A#3";
        }
        else if (frequency > 488 && frequency < 518) { /// 493
            return "B3";
        }
        else if (frequency > 518 && frequency < 549) { /// 523
            return "C4";
        }
        else if (frequency > 549 && frequency < 582) { /// 554
            return "C#4";
        }
        else if (frequency > 582 && frequency < 617) { /// 587
            return "D4";
        }
        else if (frequency > 617 && frequency < 654) { /// 622
            return "D#4";
        }
        else if (frequency > 617 && frequency < 654) { /// 659
            return "E4";
        }
        else if (frequency > 617 && frequency < 654) { /// 698
            return "F4";
        }
        else if (frequency > 617 && frequency < 654) { /// 740
            return "F#4";
        }
        else if (frequency > 617 && frequency < 654) { /// 784
            return "G4";
        }
        else if (frequency > 617 && frequency < 654) { /// 830
            return "G#4";
        }
        else if (frequency > 617 && frequency < 654) { /// 880
            return "A4";
        }
        else if (frequency > 617 && frequency < 654) { /// 932
            return "A#4";
        }
        else if (frequency > 617 && frequency < 654) { /// 988
            return "B4";
        }
        else {
            return "";
        }
        /// 4 - друга октава
    }

    public static String resolveNoteForMidiNumber(int midiNumber) {
        switch (midiNumber) {
            /// 2 - мала октава
            case 36:

                return "C2";

            case 37:

                return "C#2";

            case 38:

                return "D2";

            case 39:

                return "D#2";

            case 40:

                return "E2";

            case 41:

                return "F2";

            case 42:

                return "F#2";

            case 43:

                return "G2";

            case 44:

                return "G#2";

            case 45:

                return "A2";

            case 46:

                return "A#2";

            case 47:

                return "B2";

            ///--------------------------------

            case 48:

                return "C3";

            case 49:

                return "C#3";

            case 50:

                return "D3";

            case 51:

                return "D#3";

            case 52:

                return "E3";  /// опорно нота - Мі Першої Октави

            case 53:

                return "F3";

            case 54:

                return "F#3";

            case 55:

                return "G3";

            case 56:

                return "G#3";

            case 57:

                return "A3";

            case 58:

                return "A#3";

            case 59:

                return "B3";

            ///--------------------------------

            case 60:

                return "C4";

            case 61:

                return "C#4";

            case 62:

                return "D4";

            case 63:

                return "D#4";

            case 64:

                return "E4";

            case 65:

                return "F4";

            case 66:

                return "F#4";

            case 67:

                return "G4";

            case 68:

                return "G#4";

            case 69:

                return "A4";

            case 70:

                return "A#4";

            case 71:

                return "B4";

            /// 4 - друга октава

            default:

                return "";
        }
    }
}
