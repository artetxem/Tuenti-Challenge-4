#include <stdint.h>
#include <stdio.h>

uint8_t r[8];


void swap(int i, int j) {
	uint8_t aux = r[i];
	r[i] = r[j];
	r[j] = aux;
}


void invert() {
	swap(0, 7);
	swap(1, 6);
	swap(2, 5);
	swap(3, 4);
}


void forward() { // This is what the password checker does in each iteration prior to inverting the array
	uint8_t aux;

	r[5] ^= 0x48;

	// swap the hexadecimal digits of r[7]
	aux = r[7];
	aux &= 0x0f;
	aux <<= 0x04;
	r[7] >>= 0x04;
	r[7] &= 0x0f;
	r[7] |= aux;

	r[4] *= 0x0d;

	r[6] = -r[6];

	aux = r[3];
	aux += r[7];
	aux ^= r[1];
	aux += r[4];
	r[0] ^= aux;

	r[3] = ~r[3];

	r[2] += r[0];

	if (r[4] > 0x80) r[1] += 0x23;
}


void backward() { // This is what we have to do in order to reverse what the password checker does in each iteration (i.e. forward(); backward(); is equivalent to a NOP)
	uint8_t aux;

	if (r[4] > 0x80) r[1] -= 0x23;

	r[2] -= r[0];

	r[3] = ~r[3];

	aux = r[3];
	aux += r[7];
	aux ^= r[1];
	aux += r[4];
	r[0] ^= aux;

	r[6] = -r[6];

	for (aux = 0x00; (uint8_t)(aux*0x0d) != r[4] && aux != 0xff; aux++);
	r[4] = aux;

	// swap the hexadecimal digits of r[7]
	aux = r[7];
	aux &= 0x0f;
	aux <<= 0x04;
	r[7] >>= 0x04;
	r[7] &= 0x0f;
	r[7] |= aux;

	r[5] ^= 0x48;
}


int main(int argc, char* argv[]) {
	int i;

	/* This is what the password checker does, checking that r is db12b51423f43868 in the end
	uint32_t h, l;
	scanf("%8x%8x",&h,&l);
	r[7] = l&0xff;
	r[6] = l>>8&0xff;
	r[5] = l>>16&0xff;
	r[4] = l>>24;
	r[3] = h&0xff;
	r[2] = h>>8&0xff;
	r[1] = h>>16&0xff;
	r[0] = h>>24;
	for (i = 0; i < 50; i++) {
		forward();
		invert();
	}
	*/

	// We want r to take db12b51423f43868 in the end
	r[0] = 0xdb;
	r[1] = 0x12;
	r[2] = 0xb5;
	r[3] = 0x14;
	r[4] = 0x23;
	r[5] = 0xf4;
	r[6] = 0x38;
	r[7] = 0x68;

	// We simply have to reverse the whole process to recover the password
	for (i = 0; i < 50; i++) {
		invert();
		backward();
	}

	// Print the password
	printf("%02x%02x%02x%02x%02x%02x%02x%02x\n", r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7]);

}
