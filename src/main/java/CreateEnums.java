import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CreateEnums
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader("c:/temp/keycodes.txt"));
		String line;
		while ((line = reader.readLine()) != null)
		{

			String enumName = line;
			if (!"-".equals(enumName))
			{
				String code = null;
				String description = null;
				if (enumName.contains("_"))
				{
					code = reader.readLine();
					description = reader.readLine();
				}
				else
				{
					description = reader.readLine();
					code = new String(enumName);
					enumName = description.replace(" ", "_").toUpperCase();
				}

				System.out.println(enumName + "(" + code + ",\"" + description + "\"),");
			}
			else
			{
				reader.readLine();
				reader.readLine();
			}

		}
		reader.close();
	}
}
