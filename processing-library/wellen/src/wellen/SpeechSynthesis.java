package wellen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;

public class SpeechSynthesis {

    private final boolean mVerbose;
    private final boolean mRemoveSpecialChars;
    private boolean mBlocking = true;
    private int mWordsPerMinute = 0;
    private String mFileName = null;

    public SpeechSynthesis() {
        mVerbose = false;
        mRemoveSpecialChars = true;
    }

    public SpeechSynthesis(boolean verbose, boolean remove_special_characters) {
        mVerbose = verbose;
        mRemoveSpecialChars = remove_special_characters;
    }

    public void say(String pVoice, String pMessage, boolean pBlocking, int pWordsPerMinute, String pFileName) {
        if (mVerbose) {
            System.out.println("+++ saying: " + pMessage);
        }
        try {
            if (mRemoveSpecialChars) {
                final String[] searchList = {"ƒ", "‰", "÷", "ˆ", "‹", "¸", "ﬂ"};
                final String[] replaceList = {"Ae", "ae", "Oe", "oe", "Ue", "ue", "sz"};
                for (int i = 0; i < replaceList.length; i++) {
                    pMessage = pMessage.replace(searchList[i], replaceList[i]);
                }
                pMessage = Normalizer.normalize(pMessage, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                pMessage = pMessage.replaceAll("/[^[:alnum:]]/", " ");
            }
            String[] mCommand = new String[]{"say",
                    pVoice.isEmpty() ? "" : "-v",
                    pVoice.isEmpty() ? "" : pVoice,
                    pWordsPerMinute > 0 ? "-r" : "",
                    pWordsPerMinute > 0 ? "" + pWordsPerMinute : "",
                    (pFileName != null && !pFileName.isEmpty()) ? "-o" : "",
                    (pFileName != null && !pFileName.isEmpty()) ? pFileName : "",
                    "\"" + pMessage + "\""};
            if (mVerbose) {
                System.out.print("+++ ");
                for (String mCommandSeg : mCommand) {
                    System.out.print(mCommandSeg);
                    System.out.print(" ");
                }
                System.out.println();
            }
            final Process p = Runtime.getRuntime().exec(mCommand);
            if (pBlocking) {
                int mExit = p.waitFor();
                if (mVerbose) {
                    System.out.println("+++ exit value: " + mExit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void blocking(boolean pMakeBlocking) {
        mBlocking = pMakeBlocking;
    }

    public void setWordsPerMinute(int pWordsPerMinute) {
        mWordsPerMinute = pWordsPerMinute;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public void say(String message) {
        say("", message, mBlocking, mWordsPerMinute, mFileName);
    }

    public void say(String voice, String message) {
        say(voice, message, mBlocking, mWordsPerMinute, mFileName);
    }

    public static String[] list() {
        String[] mCommand = new String[]{"say", "-v", "?"};
        final Process p;
        try {
            p = Runtime.getRuntime().exec(mCommand);
            int mExit = p.waitFor();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            ArrayList<String> mVoices = new ArrayList<>();
            String s;
            while ((s = stdInput.readLine()) != null) {
                String[] mNames = s.split(" ");
                mVoices.add(mNames[0]);
            }
            String[] mVoiceNames = new String[mVoices.size()];
            mVoices.toArray(mVoiceNames);
            return mVoiceNames;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    /*
     * use `man say` to get a full description of the say tool.
     *
     * examplary outout of `say -v ?`
     *     Alex                en_US    # Most people recognize me by my voice.
     *     Alice               it_IT    # Salve, mi chiamo Alice e sono una voce italiana.
     *     Alva                sv_SE    # Hej, jag heter Alva. Jag är en svensk röst.
     *     Amelie              fr_CA    # Bonjour, je m’appelle Amelie. Je suis une voix canadienne.
     *     ...
     *
     */
}