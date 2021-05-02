package AI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Network {

	int[] sizes;

	double[][] neuron, bias, z, derivat;
	double[][][] weight;
	
	Random random;

	public Network(int... sizes) {
		this.sizes = sizes;

		neuron = new double[sizes.length][];
		bias = new double[sizes.length][];
		z = new double[sizes.length][];
		derivat = new double[sizes.length][];
		weight = new double[sizes.length][][];

		for (int i = 0; i < sizes.length; i++) {
			neuron[i] = new double[sizes[i]];
			z[i] = new double[sizes[i]];
			derivat[i] = new double[sizes[i]];
			if (i > 0) {
				bias[i] = new double[sizes[i]];
				weight[i] = new double[sizes[i]][sizes[i - 1]];

			}
		}
		random = new Random();
		fillRandom(-1, 1);
	}

	public Network(Network n) {
		this.sizes = n.sizes.clone();

		neuron = new double[sizes.length][];
		bias = new double[sizes.length][];
		z = new double[sizes.length][];
		derivat = new double[sizes.length][];
		weight = new double[sizes.length][][];

		for (int i = 0; i < sizes.length; i++) {
			neuron[i] = new double[sizes[i]];
			z[i] = new double[sizes[i]];
			derivat[i] = new double[sizes[i]];
			if (i > 0) {
				bias[i] = new double[sizes[i]];

				weight[i] = new double[sizes[i]][sizes[i - 1]];

			}
		}

		fill(n.bias, n.weight);
	}

	public Network(String path) {

		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(path)));

			String[] line = new String[3];
			String[][] split = new String[line.length][];
			for (int i = 0; i < line.length; i++) {
				line[i] = reader.readLine().replace("[", "").replace("]", "").replace(" ", "").replace("null,", "");
				split[i] = line[i].split(",");
			}
			sizes = new int[split.length];
			for (int i = 0; i < split.length; i++) {
				sizes[i] = Integer.parseInt(split[0][i]);
			}

			neuron = new double[sizes.length][];
			bias = new double[sizes.length][];
			z = new double[sizes.length][];
			derivat = new double[sizes.length][];
			weight = new double[sizes.length][][];

			int idx = 0;

			for (int i = 0; i < sizes.length; i++) {
				neuron[i] = new double[sizes[i]];
				z[i] = new double[sizes[i]];
				derivat[i] = new double[sizes[i]];
				if (i > 0) {
					bias[i] = new double[sizes[i]];

					weight[i] = new double[sizes[i]][sizes[i - 1]];

					int jdx = 0;
					for (int j = 0; j < sizes[i]; j++) {

						bias[i][j] = Double.parseDouble(split[1][idx + j]);

						for (int k = 0; k < sizes[i - 1]; k++) {
							weight[i][j][k] = Double.parseDouble(split[2][idx + jdx + k]);

						}
						jdx += sizes[i - 1];
					}
					idx += sizes[i];
				}

			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fillRandom(double min, double max) {
		for (int i = 1; i < sizes.length; i++) {

			for (int j = 0; j < sizes[i]; j++) {

				bias[i][j] = weight_init(i - 1);

				for (int k = 0; k < sizes[i - 1]; k++) {

					weight[i][j][k] = weight_init(i - 1);
				}

			}

		}
	}

	public void fill(double[][] b, double[][][] w) {
		for (int i = 1; i < sizes.length; i++) {

			for (int j = 0; j < sizes[i]; j++) {

				bias[i][j] = b[i][j];

				for (int k = 0; k < sizes[i - 1]; k++) {

					weight[i][j][k] = w[i][j][k];
				}

			}

		}
	}

	public double[] calc(double[] input) {
		if (input.length == sizes[0]) {
			neuron[0] = input;
			for (int i = 1; i < sizes.length; i++) {
				for (int j = 0; j < sizes[i]; j++) {
					double val = bias[i][j];
					for (int k = 0; k < sizes[i - 1]; k++) {
						val += weight[i][j][k] * neuron[i - 1][k];
					}
					neuron[i][j] = ReLU(val);
					z[i][j] = val;
				}
			}
			return neuron[sizes.length - 1];
		} else {
			return null;
		}
	}

	public void backprop(double[] output) {
		for (int i = 0; i < sizes[sizes.length - 1]; i++) {
			double o = neuron[sizes.length - 1][i];
			derivat[sizes.length - 1][i] = 2 * (o - output[i]);
		}

		for (int i = sizes.length - 2; i > 0; i--) {
			for (int j = 0; j < sizes[i]; j++) {
				int sum = 0;
				for (int k = 0; k < sizes[i + 1]; k++) {
					sum += weight[i + 1][k][j] * derivat[i + 1][k] * ReLU_derivative(z[i][j]);
				}
				derivat[i][j] = sum;
			}

		}
	}

	public void calc_gradient(double[][] b_gradient, double[][][] w_gradient, double eta, int step) {
		for (int i = 1; i < sizes.length; i++) {
			for (int j = 0; j < sizes[i]; j++) {
				double temp = -eta * derivat[i][j];
				b_gradient[i][j] = (b_gradient[i][j] * (step - 1) + temp) / step;
				for (int k = 0; k < sizes[i - 1]; k++) {
					w_gradient[i][j][k] += (w_gradient[i][j][k] * (step - 1)
							+ (neuron[i - 1][k] * temp * ReLU_derivative(z[i][j]))) / step;
				}

			}
		}
	}

	public void apply_gradient(double[][] b_gradient, double[][][] w_gradient) {
		for (int i = 1; i < sizes.length; i++) {
			for (int j = 0; j < sizes[i]; j++) {
				bias[i][j] += b_gradient[i][j];
				for (int k = 0; k < sizes[i - 1]; k++) {
					weight[i][j][k] += w_gradient[i][j][k];
				}

			}
		}

	}

	public void train(double[][] input, double[][] output, double eta, int epochs) {
		for (int epoch = 0; epoch < epochs; epoch++) {
			Random rnd = new Random();
			double[][] in = shuffle(input, rnd);
			double[][] out = shuffle(output, rnd);

			double[][] b_gradient = new double[sizes.length][];
			double[][][] w_gradient = new double[sizes.length][][];

			for (int i = 1; i < sizes.length; i++) {
				b_gradient[i] = new double[sizes[i]];
				w_gradient[i] = new double[sizes[i]][sizes[i - 1]];
			}

			for (int i = 0; i < input.length; i++) {
				calc(in[i]);
				backprop(out[i]);
				calc_gradient(b_gradient, w_gradient, eta, i + 1);
			}

			apply_gradient(b_gradient, w_gradient);
		}

	}

	public void train(double[] input, double[] output, double eta) {
		calc(input);

		backprop(output);
		update(eta);
	}

	public void update(double eta) {
		for (int i = 1; i < sizes.length; i++) {
			for (int j = 0; j < sizes[i]; j++) {
				double temp = -eta * derivat[i][j];
				bias[i][j] += temp;
				for (int k = 0; k < sizes[i - 1]; k++) {
					weight[i][j][k] += neuron[i - 1][k] * temp;
				}

			}
		}
	}

	public void save(String folder, String path, boolean folderize, boolean numerize) {
		FileOutputStream fos = null;

		for (int i = 0; true; i++) {
			File f = new File(folder + i);
			if (!f.exists()) {
				folder += folderize ? i : i - 1;
				break;
			}
		}

		File f = new File(folder);

		if (!f.exists()) {
			f.mkdir();
		}

		System.out.println(folder + "  " + f.isDirectory());

		for (int i = 0; true; i++) {
			if (!new File(folder + "/" + path + i).exists()) {
				try {
					fos = new FileOutputStream(folder + "/" + path + (numerize ? i : ""));
					fos.write((Arrays.toString(sizes) + "\n").getBytes());
					fos.write((Arrays.deepToString(bias) + "\n").getBytes());
					fos.write(Arrays.deepToString(weight).getBytes());
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public double[][] shuffle(double[][] in, Random seed) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < in.length; i++) {
			list.add(i);
		}
		Collections.shuffle(list, seed);

		double[][] neu = new double[in.length][];

		for (int i = 0; i < in.length; i++) {
			neu[i] = in[list.get(i)];
		}

		return in;
	}

	public double randDbetween(double std) {
		double num = std * random.nextGaussian();
		return num;
	}

	public double weight_init(int layer) {
		double a = Math.sqrt(2 / (double) sizes[layer]);
		return randDbetween(a);
	}

	public double ReLU(double x) {
		return x > 0 ? x : 0;
	}

	public double ReLU_derivative(double x) {
		return x > 0 ? 1 : 0;
	}
	
	public static void generate(double[][] input, double[][] output) {
		for(int i = 0; i < input.length; i++) {
			input[i] = new double[] {(double)(Math.round(Math.random()*10+1)),(double)(Math.round(Math.random()*10+1))};
			output[i] = new double[] {input[i][0]+input[i][1]};
		}
	}

	public static void main(String[] args) {
		double[][] input = new double[10][2];
		double[][] output = new double[10][1];
		
		generate(input,output);

		Network n = new Network(2, 4, 1);

		for (int i = 0; i < 100; i++) {
			int rand = (int) (Math.random() * input.length);
			n.train(input, output, 0.01, 500);

			double[] random = new double[] {(double)(Math.round(Math.random()*10+1)),(double)(Math.round(Math.random()*10+1))};
			
			System.out.println(Arrays.toString(random));
			System.out.println("ergebnis == " + Arrays.toString(n.calc(random)));
			System.out.println();
		}
		
		System.out.println(Arrays.deepToString(n.weight));
		System.out.println(Arrays.deepToString(n.bias));
	}
}
