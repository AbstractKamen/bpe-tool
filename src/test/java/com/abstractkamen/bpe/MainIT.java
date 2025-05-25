package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.IntList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.abstractkamen.bpe.commands.BpeCompressCommand.getInitialTokens;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class MainIT {

  private String inputTestFile;
  private String compressedInputTestFile;
  private String decompressedInputTestFile;
  private int maxIterations;

  private IntList tokensIn;
  private String testDirPath;
  private static final String TEST_OUTPUT_COMPRESSED_PATH_PREFIX = "compressed-";
  private static final String TEST_OUTPUT_DECOMPRESSED_PATH_PREFIX = "decompressed-";

  public MainIT(String inputTestFile, int maxIterations) throws IOException {
    final URL resource = Thread.currentThread().getContextClassLoader().getResource(inputTestFile);
    final File file = new File(resource.getFile());
    this.inputTestFile = file.toPath().toString();
    this.testDirPath = this.inputTestFile.substring(0, this.inputTestFile.length() - inputTestFile.length());
    this.compressedInputTestFile = testDirPath + TEST_OUTPUT_COMPRESSED_PATH_PREFIX + file.getName();
    this.decompressedInputTestFile = testDirPath + TEST_OUTPUT_DECOMPRESSED_PATH_PREFIX + file.getName();
    this.maxIterations = maxIterations;

    try (final InputStream resourceAsStream = resource.openStream()) {
      this.tokensIn = getInitialTokens(resourceAsStream);
    }
  }

  @Before
  public void beforeTest() {
    cleanFile(compressedInputTestFile, "before");
    cleanFile(decompressedInputTestFile, "before");
  }

  @After
  public void afterTest() {
    cleanFile(compressedInputTestFile, "after");
    cleanFile(decompressedInputTestFile, "after");
  }

  @Parameters(name = "{index}: file:{0}, iterations:{1}")
  public static Iterable<Object[]> data() {
    final List<Object[]> params = new ArrayList<>();
    int p = 1;
    // case 1
    final int maxIterations1 = 15;
    for (int i = 1; i < maxIterations1; i += p) {
      p += Math.max(1, (int) Math.pow(Math.E, ((double) i) / maxIterations1));
      params.add(new Object[]{"test-io/simple.txt", i});
    }
    // case 2
    final int maxIterations2 = 450;
    for (int i = 1; i < maxIterations2; i += p) {
      p += Math.max(1, (int) Math.pow(Math.E, ((double) i) / maxIterations2));
      params.add(new Object[]{"test-io/bpe-wiki-page.txt", i});
    }
    // case 3
    final int maxIterations3 = 160;
    for (int i = 1; i < maxIterations3; i += p) {
      p += Math.max(1, (int) Math.pow(Math.E, ((double) i) / maxIterations3));
      params.add(new Object[]{"test-io/IntList-java.txt", i});
    }
    // case 4
    final int maxIterations4 = 500;
    for (int i = 1; i < maxIterations4; i += p) {
      p += Math.max(1, (int) Math.pow(Math.E, ((double) i) / maxIterations4));
      params.add(new Object[]{"test-io/labeled_data-from-hate-speech-and-offensive-language.csv", i});
    }
    return params;
  }

  @Test(timeout = 10000)
  public void compressingThenDecompressingText_shouldProduceSameText_asInput_andCompressedText_shouldBeLesserThanOrEqualTo_inputTextSize() throws IOException {
    // arrange
    // act
    Main.main(new String[]{
      "bpec",
      "-in", inputTestFile,
      "-out", compressedInputTestFile,
      "-i", maxIterations + "",
      "-charset", "utf8"
      //      ,
      //      "-debug",
    });
    Main.main(new String[]{
      "bped",
      "-in", compressedInputTestFile,
      "-out", decompressedInputTestFile,
      "-i", maxIterations + ""
      //      ,
      //      "-debug",

    });
    // assert
    final IntList compressedTokens = getInitialTokens(Files.newInputStream(Paths.get(compressedInputTestFile)));
    final IntList decompressedTokens = getInitialTokens(Files.newInputStream(Paths.get(decompressedInputTestFile)));
    final String actual = decompressedTokens.toActualString();
    final String expected = tokensIn.toActualString();
    System.out.printf("At %d iterations%nOriginal text chars: %d%nActual  text chars: %d%n", maxIterations, tokensIn.size(), compressedTokens.size());
    assertEquals(expected, actual);

  }

  private static void cleanFile(String path, String when) {
    final File file = Paths.get(path).toFile();
    if (file.exists() && file.delete()) {
      System.out.printf("clean %s up %s test%n", path, when);
    } else {
      System.out.println("Nothing to clean at " + path);
    }
  }
}
