package org.jiserte.leaftion.math;

import java.util.ArrayList;
import java.util.List;

public class ModelEvaluator {

	private int iterations;

	private double[] x;
	private double[] y;
	private double lastDiff;
	private int from;
	private int to;

	public ModelEvaluator(int iterations, double[] x, double[] y, int from, int to) {
		super();
		this.iterations = iterations;
		this.x = x;
		this.y = y;
		this.lastDiff = Double.MAX_VALUE;
		this.from = from;
		this.to= to;
	}

	public CosineModel optimize() {

		CosineModel model = new CosineModel();

		int reportPeriod = (int) (this.iterations / 50);

		this.lastDiff = model.diff(x, y, from, to);

		List<Double> objectiveSeries = new ArrayList<>();
		List<Integer> acceptedItaration = new ArrayList<>();

		for (int i = 0; i < this.iterations; i++) {

			CosineModel newModel = model.mutate();

			double diff = newModel.diff(x, y, from, to);

			if (diff < this.lastDiff) {

				model = newModel;
				this.lastDiff = diff;
			}
			
			if ( (i + 1) % reportPeriod == 0) {
				objectiveSeries.add(this.lastDiff);
				acceptedItaration.add(i);
			}

		}

		model.setAcceptedIterations(new int[acceptedItaration.size()]);
		model.setObjectiveSeries(new double[objectiveSeries.size()]);

		for (int i = 0; i < objectiveSeries.size(); i++) {
			model.getAcceptedIterations()[i] = acceptedItaration.get(i);
			model.getObjectiveSeries()[i] = objectiveSeries.get(i);
		}

		return model;

	}

	public static void main(String[] args) {

		double[] cy = new double[] { 0.28466, 0.25873, 0.22794, 0.18523, 0.13143, 0.07153, 0.01203, -0.03962, -0.08096,
				-0.1109, -0.12281, -0.1127, -0.09096, -0.06184, -0.02843, 0.01299, 0.05703, 0.09932, 0.13921, 0.17453,
				0.19837, 0.22467, 0.23866, 0.24028, 0.2274, 0.20417, 0.16502, 0.12025, 0.07149, 0.02223, -0.02245,
				-0.06434, -0.0988, -0.1288, -0.15001, -0.15639, -0.15811, -0.15014, -0.12969, -0.09798, -0.05923,
				-0.01671, 0.03039, 0.07733, 0.1175, 0.14044, 0.14909, 0.14447, 0.12282, 0.09816, 0.07341, 0.05036,
				0.03042, 0.01451, 0.01103, 0.00194, -0.03161, -0.07909, -0.09683, -0.09368, -0.08465, -0.06847,
				-0.04111, -0.00574, 0.03414, 0.07954, 0.12739, 0.15756, 0.19423, 0.22121, 0.26315, 0.26968, 0.26036,
				0.24885, 0.23733, 0.23629, 0.24178, 0.20108, 0.11823, 0.03644, -0.02639, -0.07041, -0.09312, -0.09532,
				-0.07636, -0.04467, 0.0023, 0.04549, 0.06787, 0.09308, 0.16549, 0.24212, 0.28863, 0.30098, 0.29932,
				0.28609, 0.27057, 0.23423, 0.19647, 0.15389, 0.12504, 0.09602, 0.05371, -0.0096, -0.05578, -0.06541,
				-0.03708, -0.00405, 0.00586, 0.01509, 0.0373, 0.06614, 0.09895, 0.14385, 0.19709, 0.24028, 0.27704,
				0.30066, 0.29341, 0.25291, 0.19905, 0.18935, 0.20389, 0.18857, 0.15228, 0.10369, 0.06574, 0.01793,
				-0.04555, -0.08328, -0.09028, -0.08304, -0.07289, -0.04312, -0.01097, 0.00682, 0.03758, 0.09354,
				0.16077, 0.19827, 0.21197, 0.2187, 0.2016, 0.16212, 0.11943, 0.08942, 0.06648, 0.03128, 0.00453,
				-0.01325, -0.03369, -0.05435, -0.06204, -0.0441, 0.00359, 0.0433, 0.08936, 0.12806, 0.17503, 0.22202,
				0.26074, 0.27611, 0.27915, 0.26734, 0.23605, 0.20023, 0.1528, 0.09349, 0.04224, -0.00843, -0.04799,
				-0.08577, -0.10797, -0.11751, -0.12501, -0.10984, -0.07267, -0.02579, 0.02695, 0.07582, 0.12434,
				0.17354, 0.22114, 0.26761, 0.31287, 0.33416, 0.33886, 0.34533, 0.33059, 0.29092, 0.23553, 0.16886,
				0.10579, 0.04483, -0.02062, -0.07916, -0.12337, -0.16186, -0.18476, -0.18109, -0.16346, -0.13354,
				-0.09041, -0.04554, 0.01699, 0.08642, 0.14918, 0.20153, 0.25246, 0.29403, 0.30876, 0.30153, 0.28006,
				0.23779, 0.19121, 0.14068, 0.09695, 0.04694, -0.01285, -0.07136, -0.10851, -0.1372, -0.16397, -0.1673,
				-0.1358, -0.06945, -0.00775, 0.0514, 0.11588, 0.18003, 0.23747, 0.29241, 0.33723, 0.36757, 0.38572,
				0.39591, 0.39127, 0.36901, 0.31093, 0.25589, 0.19959, 0.13308, 0.06355 };
		double[] cx = new double[cy.length];

		double avg = 0;
		double max = -1;
		double min = 1;
		for (int i = 0; i < cy.length; i++) {
			avg += cy[i];
			max = Math.max(max, cy[i]);
			min = Math.min(min, cy[i]);
		}
		avg = avg / cy.length;
		for (int i = 0; i < cy.length; i++) {
			cy[i] -= avg;
		}
		max = max - avg;
		min = min - avg;
		double amp = (max - min) / 2;

		// System.out.println(avg);
		// System.out.println(max);
		// System.out.println(min);
		// System.out.println(amp);
		for (int i = 0; i < cy.length; i++) {
			cy[i] = cy[i] / amp;
		}

		//
		// for (int i = 0; i< cy.length; i++) {
		// System.out.println(cy[i]); ;
		// }

		for (int i = 0; i < cx.length; i++) {
			cx[i] = i;
		}

		ModelEvaluator me = new ModelEvaluator(10000, cx, cy, 0, cx.length-1);

		CosineModel a = me.optimize();

		System.out.println(a);

	}

}
