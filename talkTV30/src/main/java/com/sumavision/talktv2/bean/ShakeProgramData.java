package com.sumavision.talktv2.bean;

import java.util.ArrayList;
import java.util.List;

public class ShakeProgramData {

	public long id;
	public String name,pic,director,area,intro,progPic;
	public int programEvaluateSum;
	public boolean isZan,hasReported;
	public List<ShakeSub> programSub = new ArrayList<ShakeSub>();
}
