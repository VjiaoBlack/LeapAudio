
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.AudioFileFormat;

import gnu.getopt.Getopt;


public class AudioLoop extends Thread {

	private static boolean	DEBUG;

	private static final int	DEFAULT_INTERNAL_BUFSIZ = 40960;
	private static final int	DEFAULT_EXTERNAL_BUFSIZ = 40960;

	private TargetDataLine	m_targetLine;
	private SourceDataLine	m_sourceLine;
	private boolean		m_bRecording;
	private int		m_nExternalBufferSize;

	public AudioLoop(AudioFormat format,
			 int nInternalBufferSize,
			 int nExternalBufferSize,
			 String strMixerName)
		throws	LineUnavailableException
	{
		Mixer		mixer = null;
		if (strMixerName != null)
		{
			Mixer.Info	mixerInfo = AudioCommon.getMixerInfo(strMixerName);
			if (DEBUG) { out("AudioLoop.<init>(): mixer info: " + mixerInfo); }
			mixer = AudioSystem.getMixer(mixerInfo);
			if (DEBUG) { out("AudioLoop.<init>(): mixer: " + mixer); }
		}

		DataLine.Info	targetInfo = new DataLine.Info(TargetDataLine.class, format, nInternalBufferSize);
		DataLine.Info	sourceInfo = new DataLine.Info(SourceDataLine.class, format, nInternalBufferSize);
		if (mixer != null)
		{
			m_targetLine = (TargetDataLine) mixer.getLine(targetInfo);
			m_sourceLine = (SourceDataLine) mixer.getLine(sourceInfo);
		}
		else
		{
			m_targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			m_sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
		}
		if (DEBUG) { out("AudioLoop.<init>(): SourceDataLine: " + m_sourceLine); }
		if (DEBUG) { out("AudioLoop.<init>(): TargetDataLine: " + m_targetLine); }
		m_targetLine.open(format, nInternalBufferSize);
		m_sourceLine.open(format, nInternalBufferSize);
		m_nExternalBufferSize = nExternalBufferSize;
		}



	public void start()
	{
		m_targetLine.start();
		m_sourceLine.start();
		super.start();
	}



  public void stopRecording() {
	  m_line.stop();
	  m_line.close();
	  m_bRecording = false;
  }




	public void run()
	{
		byte[]	abBuffer = new byte[m_nExternalBufferSize];
		int	nBufferSize = abBuffer.length;
		m_bRecording = true;
		while (m_bRecording)
		{
			if (DEBUG) { out("Trying to read: " + nBufferSize); }

			int	nBytesRead = m_targetLine.read(abBuffer, 0, nBufferSize);
			if (DEBUG) { out("Read: " + nBytesRead); }

			m_sourceLine.write(abBuffer, 0, nBytesRead);
		}
	}



	public static void main(String[] args)
	{
		String	strMixerName = null;
		float	fFrameRate = 44100.0F;
		int	nInternalBufferSize = DEFAULT_INTERNAL_BUFSIZ;
		int	nExternalBufferSize = DEFAULT_EXTERNAL_BUFSIZ;

		Getopt	g = new Getopt("AudioLoop", args, "hlr:i:e:M:D");
		int	c;
		while ((c = g.getopt()) != -1)
		{
			switch (c)
			{
			case 'h':
				printUsageAndExit();

			case 'l':
				AudioCommon.listMixersAndExit();

			case 'r':
				fFrameRate = Float.parseFloat(g.getOptarg());
				if (DEBUG) { out("AudioLoop.main(): frame rate: " + fFrameRate); }
				break;

			case 'i':
				nInternalBufferSize = Integer.parseInt(g.getOptarg());
				if (DEBUG) { out("AudioLoop.main(): internal buffer size: " + nInternalBufferSize); }
				break;

			case 'e':
				nExternalBufferSize = Integer.parseInt(g.getOptarg());
				if (DEBUG) { out("AudioLoop.main(): external buffer size: " + nExternalBufferSize); }
				break;

			case 'M':
				strMixerName = g.getOptarg();
				if (DEBUG) { out("AudioLoop.main(): mixer name: " + strMixerName); }
				break;

			case 'D':
				DEBUG = true;
				break;

			case '?':
				printUsageAndExit();

			default:
				out("AudioLoop.main(): getopt() returned: " + c);
				break;
			}
		}
		AudioFormat	audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, fFrameRate, 16, 2, 4, fFrameRate, false);
		if (DEBUG) { out("AudioLoop.main(): audio format: " + audioFormat); }
		AudioLoop	audioLoop = null;
		try
		{
			audioLoop = new AudioLoop(audioFormat,
						  nInternalBufferSize,
						  nExternalBufferSize,
						  strMixerName);
		}
		catch (LineUnavailableException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		audioLoop.start();
	}



	private static void printUsageAndExit()
	{
		out("AudioLoop: usage:");
		out("\tjava AudioLoop -h");
		out("\tjava AudioLoop -l");
		out("\tjava AudioLoop [-D] [-M <mixername>] [-e <buffersize>] [-i <buffersize>]");
		System.exit(1);
	}



	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}
}



/*** AudioLoop.java ***/
