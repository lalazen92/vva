package vav.cyberspace.viettel.vva.flacEncoder;

import net.sourceforge.javaFlacEncoder.FLAC_FileEncoder;

import java.io.File;
/*************************************************************************************************************
 * Class that contains methods to encode Wave files to FLAC files
 * THIS IS THANKS TO THE javaFlacEncoder Project created here: http://sourceforge.net/projects/javaflacencoder/
 ************************************************************************************************************/
public class Wav2Flac {

    /**
     * Constructor
     */
    public Wav2Flac() {

    }

    /**
     * Converts a wave file to a FLAC file(in order to POST the data to Google and retrieve a response) <br>
     * Sample Rate is 8000 by default
     *

     */
    public void convertWavToFlac(String wavFilename, String flacFilename) {
        // TODO Auto-generated method stub
        FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();   // <---- Error
        File inputFile = new File(wavFilename);
        File outputFile = new File(flacFilename);
        flacEncoder.adjustAudioConfig(8000, 16, 1);
        flacEncoder.encode(inputFile, outputFile);

    }
}
