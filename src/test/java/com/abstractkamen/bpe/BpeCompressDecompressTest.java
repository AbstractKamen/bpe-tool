package com.abstractkamen.bpe;

import com.abstractkamen.bpe.structures.BytePair;
import com.abstractkamen.bpe.structures.IntList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.abstractkamen.bpe.Main.getInitialTokens;

@RunWith(Parameterized.class)
public class BpeCompressDecompressTest {

  private String inputTestFile;
  private int maxIterations;

  private IntList tokensIn;
  private String expectedText;

  public BpeCompressDecompressTest(String inputTestFile, int maxIterations) throws IOException {
    this.inputTestFile = inputTestFile;
    this.maxIterations = maxIterations;

    try (final InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(inputTestFile)) {
      this.tokensIn = getInitialTokens(resourceAsStream);
      this.expectedText = tokensIn.toActualString();
    }
  }

  @Parameters(name = "{index}: file:{0}, iterations:{1}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(
      new Object[]{"test-input/bpe-wiki-page.txt", 1}, new Object[]{"test-input/IntList-java.txt", 1},
      new Object[]{"test-input/bpe-wiki-page.txt", 2}, new Object[]{"test-input/IntList-java.txt", 2},
      new Object[]{"test-input/bpe-wiki-page.txt", 5}, new Object[]{"test-input/IntList-java.txt", 3},
      new Object[]{"test-input/bpe-wiki-page.txt", 25}, new Object[]{"test-input/IntList-java.txt", 5},
      new Object[]{"test-input/bpe-wiki-page.txt", 50}, new Object[]{"test-input/IntList-java.txt", 20},
      new Object[]{"test-input/bpe-wiki-page.txt", 75}, new Object[]{"test-input/IntList-java.txt", 25},
      new Object[]{"test-input/bpe-wiki-page.txt", 100}, new Object[]{"test-input/IntList-java.txt", 30}
    );
  }

  @Test(timeout = 1000)
  public void compressingThenDecompressingText_shouldProduceSameText_asInput_andCompressedText_shouldBeLesserThanOrEqualTo_inputTextSize() {
    // arrange

    // act
    final BpeCompress bpeCompress = new BpeCompress();
    
    bpeCompress.compressTokens(tokensIn, maxIterations);

    final List<BytePair> pairs = bpeCompress.getPairs();
    final IntList compressedTokens = bpeCompress.getCompressedTokens();
    final BpeDecompress bpeDecompress = new BpeDecompress();
    final String compressedTokensActualString = compressedTokens.toActualString();
    // assert
    bpeDecompress.decompressTokens(compressedTokens, pairs);
    final IntList decompressedTokens = bpeDecompress.getDecompressedTokens();
    final String decompressedText = decompressedTokens.toActualString();
    Assert.assertTrue(expectedText.length() >= compressedTokensActualString.length());
    Assert.assertEquals(expectedText, decompressedText);
  }
}
