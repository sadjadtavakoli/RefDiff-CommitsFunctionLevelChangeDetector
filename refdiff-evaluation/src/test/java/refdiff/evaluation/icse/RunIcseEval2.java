package refdiff.evaluation.icse;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import refdiff.core.diff.RastComparator;
import refdiff.evaluation.EvaluationUtils;
import refdiff.evaluation.KeyPair;
import refdiff.evaluation.RefactoringSet;
import refdiff.evaluation.RefactoringType;
import refdiff.evaluation.ResultComparator;
import refdiff.parsers.java.JavaParserNoBindings;

public class RunIcseEval2 {
	
	private EnumSet<RefactoringType> refactoringTypes = EnumSet.complementOf(EnumSet.of(RefactoringType.PULL_UP_ATTRIBUTE, RefactoringType.PUSH_DOWN_ATTRIBUTE, RefactoringType.MOVE_ATTRIBUTE));
	private EvaluationUtils evalUtils;
	
	public RunIcseEval2(String tempFolder) {
		evalUtils = new EvaluationUtils(new RastComparator(new JavaParserNoBindings()), tempFolder);
	}

	public static void main(String[] args) throws Exception {
		new RunIcseEval2(args.length > 0 ? args[0] : "D:/refdiff/").run();
	}
	
	public void run() throws Exception {
		IcseDataset data = new IcseDataset();
		List<RefactoringSet> expected = data.getExpected();
		
		ResultComparator rc = EvaluationCsvReader.buildResultComparator(data, EvaluationCsvReader.readRefDiffResults());
		
		Set<String> whitelist = new HashSet<>(Arrays.asList(
			"abbf32571232db81a5343db17a933a9ce6923b44",
			"18a7bd1fd1a83b3b8d1b245e32f78c0b4443b7a7",
			"04bcfe98dbe7b05e508559930c21379ece845732",
			"446e2537895c15b404a74107069a12f3fc404b15",
			"d3533c1a0716ca114d294b3ea183504c9725698f",
			"9de5f0d408f861455716b8410fd53f62b360787d",
			"364f50274d4b4b83d40930c0d2c4d0e57fb34589",
			"e78cda0fcf23de3973b659bc54f58a4e9b1f3bd3",
			"bf35b533f067b51d4c373c5e5124d88525db99f3",
			"54fa890a6af4ccf564fb481d3e1b6ad4d084de9e",
			"021d17c8234904dcb1d54596662352395927fe7b",
			"cb98ee25ff52bf97faebe3f45cdef0ced9b4416e",
			"bf5ee44b3b576e01ab09cae9f50300417b01dc07",
			"881baed894540031bd55e402933bcad28b74ca88",
			"b36ab386559d04db114db8edd87c8d4cbf850c12",
			"c753d2e41ba667f9b5a31451a16ecbaecdc65d80",
			"c7b6a7aa878aabd6400d2df0490e1eb2b810c8f9",
			"23c49d834d3859fc76a604da32d1789d2e863303",
			"2b89553db5081fe4e55b7b34d636d0ea2acf71c5",
			"69dd55c93fc99c5f7a1e2c21f10e671e311be49e",
			"5a37c2aa596377cb4c9b6f916614407fd0a7d3db",
			"e813a0be86c87366157a0201e6c61662cadee586",
			"72b5348307d86b1a118e546c24d97f1ac1895bdb",
			"46b0d84de9c309bca48a99e572e6611693ed5236",
			"03ade425dd5a65d3a713d5e7d85aa7605956fbd2",
			"b0938501f1014cf663e33b44ed5bb9b24d19a358"
			));
		
		for (RefactoringSet rs : expected) {
			String project = rs.getProject();
			String commit = rs.getRevision();
			if (!whitelist.contains(commit)) {
				rc.remove(project, commit);
			} else {				
				try {
					//evalUtils.prepareSourceCodeNoCheckout(project, commit);
				} catch (RuntimeException e) {
					System.out.println(String.format("Skipped %s %s", project, commit));
					System.err.println(e.getMessage());
					continue;
				}
				Map<KeyPair, String> explanations = new HashMap<>();
				rc.compareWith("RefDiff", evalUtils.runRefDiffGit(project, commit, explanations), explanations);
			}
		}
		
		rc.printSummary(System.out, refactoringTypes);
		rc.printDetails(System.out, refactoringTypes, "RefDiff");
	}
	
}
