import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class AudioCommon {
	private static boolean		DEBUG = false;



	public static void setDebug(boolean bDebug) {
		DEBUG = bDebug;
	}

	public static void listSupportedTargetTypes()
	{
		String	strMessage = "Supported target types:";
		AudioFileFormat.Type[]	aTypes = AudioSystem.getAudioFileTypes();
		for (int i = 0; i < aTypes.length; i++)
		{
			strMessage += " " + aTypes[i].getExtension();
		}
		out(strMessage);
	}

	public static AudioFileFormat.Type findTargetType(String strExtension)
	{
		AudioFileFormat.Type[]	aTypes = AudioSystem.getAudioFileTypes();
		for (int i = 0; i < aTypes.length; i++)
		{
			if (aTypes[i].getExtension().equals(strExtension))
			{
				return aTypes[i];
			}
		}
		return null;
	}

	public static void listMixersAndExit()
	{
		out("Available Mixers:");
		Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			out(aInfos[i].getName());
		}
		if (aInfos.length == 0)
		{
			out("[No mixers available]");
		}
		System.exit(0);
	}

	public static void listMixersAndExit(boolean bPlayback)
	{
		out("Available Mixers:");
		Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			Mixer mixer = AudioSystem.getMixer(aInfos[i]);
			Line.Info lineInfo = new Line.Info(bPlayback ?
											   SourceDataLine.class :
											   TargetDataLine.class);
			if (mixer.isLineSupported(lineInfo))
			{
				out(aInfos[i].getName());
			}
		}
		if (aInfos.length == 0)
		{
			out("[No mixers available]");
		}
		System.exit(0);
	}

	public static Mixer.Info getMixerInfo(String strMixerName)
	{
		Mixer.Info[]	aInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < aInfos.length; i++)
		{
			if (aInfos[i].getName().equals(strMixerName))
			{
				return aInfos[i];
			}
		}
		return null;
	}

	public static TargetDataLine getTargetDataLine(String strMixerName,
							AudioFormat audioFormat,
							int nBufferSize)
	{

		TargetDataLine	targetDataLine = null;
		DataLine.Info	info = new DataLine.Info(TargetDataLine.class,
							 audioFormat, nBufferSize);
		try
		{
			if (strMixerName != null)
			{
				Mixer.Info	mixerInfo = getMixerInfo(strMixerName);
				if (mixerInfo == null)
				{
					out("AudioCommon.getTargetDataLine(): mixer not found: " + strMixerName);
					return null;
				}
				Mixer	mixer = AudioSystem.getMixer(mixerInfo);
				targetDataLine = (TargetDataLine) mixer.getLine(info);
			}
			else
			{
				if (DEBUG) { out("AudioCommon.getTargetDataLine(): using default mixer"); }
				targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
			}

			if (DEBUG) { out("AudioCommon.getTargetDataLine(): opening line..."); }
			targetDataLine.open(audioFormat, nBufferSize);
			if (DEBUG) { out("AudioCommon.getTargetDataLine(): opened line"); }
		}
		catch (LineUnavailableException e)
		{
			if (DEBUG) { e.printStackTrace(); }
		}
		catch (Exception e)
		{
			if (DEBUG) { e.printStackTrace(); }
		}
			if (DEBUG) { out("AudioCommon.getTargetDataLine(): returning line: " + targetDataLine); }
		return targetDataLine;
	}

	public static boolean isPcm(AudioFormat.Encoding encoding)
	{
		return encoding.equals(AudioFormat.Encoding.PCM_SIGNED)
			|| encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED);
	}

	private static void out(String strMessage)
	{
		System.out.println(strMessage);
	}



}

