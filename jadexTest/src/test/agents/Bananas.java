package test.agents;

public class Bananas {

	private String _word;
	private String _eword;
	
	public Bananas(String word, String eword){
		setEword(eword);
		setWord(word);
	}
	//{"type":"TRANSLATE","content":"{\"_eword\":\"cona\",\"_word\":\"cona\"}"}
	public void setWord(String _word) {
		this._word = _word;
	}

	public String getWord() {
		return _word;
	}

	public void setEword(String _eword) {
		this._eword = _eword;
	}

	public String getEword() {
		return _eword;
	}
	
}
