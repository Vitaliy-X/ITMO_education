#include "return_codes.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef LIBDEFLATE
#include <libdeflate.h>
#elif defined(ZLIB)
#include <zlib.h>
#elif defined(ISAL)
#include <include/igzip_lib.h>
#else
#error "No macro defined or macros specified incorrectly"
#endif

typedef unsigned char byte;

struct Chunk
{
	size_t size;
	byte len[4];
	byte name[4];
	byte* data;
	byte crc[4];
};

typedef struct {
	unsigned char red;
	unsigned char green;
	unsigned char blue;
} Color;

struct Chunk readChunk(FILE* in)
{
	struct Chunk curChunk;
	fread(curChunk.len, 4, 1, in);
	uint32_t size = 0;
	memcpy(&size, curChunk.len, 4);
	curChunk.size = ntohl(size);
	fread(curChunk.name, 4, 1, in);
	curChunk.data = malloc(curChunk.size);
	if (curChunk.data == NULL)
	{
		return curChunk;
	}
	fread(curChunk.data, curChunk.size, 1, in);
	fread(curChunk.crc, 4, 1, in);
	return curChunk;
}

struct Chunk read_plte_chunk(FILE* file)
{
	struct Chunk curChunk;
	while (1)
	{
		curChunk = readChunk(file);
		if (strncmp((char*)curChunk.name, "PLTE", 4) == 0)
		{
			break;
		}
		free(curChunk.data);
	}
	return curChunk;
}

byte* filter1(byte* line, size_t length, int type)
{
	if (type == 0 || type == 3)
	{
		byte previous = 0;
		for (size_t i = 0; i < length; i++)
		{
			line[i] = (line[i] + previous) % 256;
			previous = line[i];
		}
	}
	if (type == 2)
	{
		byte prev1 = 0, prev2 = 0, prev3 = 0;
		for (size_t i = 0; i < length; i += 3)
		{
			line[i] = (line[i] + prev1) % 256;
			prev1 = line[i];
			line[i + 1] = (line[i + 1] + prev2) % 256;
			prev2 = line[i + 1];
			line[i + 2] = (line[i + 2] + prev3) % 256;
			prev3 = line[i + 2];
		}
	}
	return line;
}

byte* filter2(byte* line, const byte* previousLine, size_t length, int type)
{
	if (type == 0 || type == 3)
	{
		for (size_t i = 0; i < length; i++)
		{
			line[i] = (line[i] + previousLine[i]) % 256;
		}
	}
	if (type == 2)
	{
		for (size_t i = 0; i < length; i += 3)
		{
			line[i] = (line[i] + previousLine[i]) % 256;
			line[i + 1] = (line[i + 1] + previousLine[i + 1]) % 256;
			line[i + 2] = (line[i + 2] + previousLine[i + 2]) % 256;
		}
	}
	return line;
}

byte* filter3(byte* line, const byte* previousLine, size_t length, int type)
{
	if (type == 0 || type == 3)
	{
		byte prev = 0;
		for (size_t i = 0; i < length; i++)
		{
			line[i] = (line[i] + (prev + previousLine[i]) / 2) % 256;
			prev = line[i];
		}
	}
	if (type == 2)
	{
		byte prev1 = 0, prev2 = 0, prev3 = 0;
		for (size_t i = 0; i < length; i += 3)
		{
			line[i] = (line[i] + (prev1 + previousLine[i]) / 2) % 256;
			prev1 = line[i];
			line[i + 1] = (line[i + 1] + (prev2 + previousLine[i + 1]) / 2) % 256;
			prev2 = line[i + 1];
			line[i + 2] = (line[i + 2] + (prev3 + previousLine[i + 2]) / 2) % 256;
			prev3 = line[i + 2];
		}
	}
	return line;
}

byte* filter4(byte* line, const byte* previousLine, size_t length, int type)
{
	if (type == 0 || type == 3)
	{
		byte prev = 0;
		for (size_t i = 0; i < length; i++)
		{
			int a = (int)prev;
			int b = (int)previousLine[i];
			int c = i == 0 ? 0 : (int)previousLine[i - 1];
			int p = a + b - c;
			int pa = abs(p - a);
			int pb = abs(p - b);
			int pc = abs(p - c);
			if (pa <= pb && pa <= pc)
				line[i] = (line[i] + a) % 256;
			else if (pb <= pc)
				line[i] = (line[i] + b) % 256;
			else
				line[i] = (line[i] + c) % 256;
			prev = line[i];
		}
	}
	if (type == 2)
	{
		byte prev1 = 0, prev2 = 0, prev3 = 0;
		for (size_t i = 0; i < length; i += 3)
		{
			int a1 = (int)prev1;
			int b1 = (int)previousLine[i];
			int c1 = i < 3 ? 0 : (int)previousLine[i - 3];
			int p1 = a1 + b1 - c1;
			int pa1 = abs(p1 - a1);
			int pb1 = abs(p1 - b1);
			int pc1 = abs(p1 - c1);
			if (pa1 <= pb1 && pa1 <= pc1)
				line[i] = (line[i] + a1) % 256;
			else if (pb1 <= pc1)
				line[i] = (line[i] + b1) % 256;
			else
				line[i] = (line[i] + c1) % 256;
			prev1 = line[i];

			int a2 = (int)prev2;
			int b2 = (int)previousLine[i + 1];
			int c2 = i < 2 ? 0 : (int)previousLine[i - 2];
			int p2 = a2 + b2 - c2;
			int pa2 = abs(p2 - a2);
			int pb2 = abs(p2 - b2);
			int pc2 = abs(p2 - c2);
			if (pa2 <= pb2 && pa2 <= pc2)
				line[i + 1] = (line[i + 1] + a2) % 256;
			else if (pb2 <= pc2)
				line[i + 1] = (line[i + 1] + b2) % 256;
			else
				line[i + 1] = (line[i + 1] + c2) % 256;
			prev2 = line[i + 1];

			int a3 = (int)prev3;
			int b3 = (int)previousLine[i + 2];
			int c3 = i < 1 ? 0 : (int)previousLine[i - 1];
			int p3 = a3 + b3 - c3;
			int pa3 = abs(p3 - a3);
			int pb3 = abs(p3 - b3);
			int pc3 = abs(p3 - c3);
			if (pa3 <= pb3 && pa3 <= pc3)
				line[i + 2] = (line[i + 2] + a3) % 256;
			else if (pb3 <= pc3)
				line[i + 2] = (line[i + 2] + b3) % 256;
			else
				line[i + 2] = (line[i + 2] + c3) % 256;
			prev3 = line[i + 2];
		}
	}
	return line;
}

int apply_palette(int width, int height, byte** image, int palette_size, const Color *palette) {
	for (int y = 0; y < height; ++y) {
		for (int x = 0; x < width; ++x) {
			int index = image[y][x];
			if (index >= palette_size) {
				fprintf(stderr, "Error: invalid color index %d\n", index);
				return 1;
			}
			Color color = palette[index];
			image[y][3 * x] = color.red;
			image[y][3 * x + 1] = color.green;
			image[y][3 * x + 2] = color.blue;
		}
	}
	return 0;
}

int apply_palette_to_row(int width, byte* row, int palette_size, const Color *palette) {
	for (int x = 0; x < width; ++x) {
		int index = row[x];
		if (index >= palette_size) {
			fprintf(stderr, "Error: invalid color index %d\n", index);
			return 0;
		}
		Color color = palette[index];
		row[3 * x] = color.red;
		row[3 * x + 1] = color.green;
		row[3 * x + 2] = color.blue;
	}
	return 1;
}

int filter(int width, int height, int colorType, int pixel, struct Color *palette, byte* const * imageMap, FILE* out)
{
	if (colorType == 3)
	{
		int check = apply_palette(width, height, imageMap, 256, &palette);
		if (check)
		{
			return ERROR_DATA_INVALID;
		}
		for (int i = 0; i < height; ++i)
		{
			byte* tmp = imageMap[i] + 1;
			fwrite(tmp, width * pixel, 1, out);
		}
	}
	else
		for (int i = 0; i < height; i++)
		{
			int filter = imageMap[i][0];
			switch (filter)
			{
			case 0:
			{
				byte* tmp = imageMap[i] + 1;
				apply_palette_to_row(width, imageMap[i], 256, palette);
				fwrite(tmp, width * pixel, 1, out);
				break;
			}
			case 1:
			{
				byte* tmp = filter1(imageMap[i] + 1, width * pixel, colorType);
				fwrite(tmp, width * pixel, 1, out);
				break;
			}
			case 2:
			{
				byte* tmp = filter2(imageMap[i] + 1, imageMap[i - 1] + 1, width * pixel, colorType);
				fwrite(tmp, width * pixel, 1, out);
				break;
			}
			case 3:
			{
				byte* tmp = filter3(imageMap[i] + 1, imageMap[i - 1] + 1, width * pixel, colorType);
				fwrite(tmp, width * pixel, 1, out);
				break;
			}
			case 4:
			{
				byte* tmp = filter4(imageMap[i] + 1, imageMap[i - 1] + 1, width * pixel, colorType);
				fwrite(tmp, width * pixel, 1, out);
				break;
			}
			}
		}
	return SUCCESS;
}

int check_PNG_format(FILE* input)
{
	char buffer[8];
	if (fread(&buffer[0], sizeof(char), 8, input) != 8)
	{
		return ERROR_DATA_INVALID;
	}
	if (strncmp(&buffer[0], "\x89\x50\x4e\x47\x0d\x0a\x1a\x0a", 8) != 0)
	{
		return ERROR_DATA_INVALID;
	}
	return 0;
}

int main(int argc, char** argv)
{
	if (argc != 3)
	{
		fprintf(stderr, "Error: you need to pass only the input and output file as arguments\n");
		return ERROR_PARAMETER_INVALID;
	}
	FILE* in = fopen(argv[1], "rb");
	if (in == NULL)
	{
		fprintf(stderr, "Error: Could not open input file \"%s\n", argv[1]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	if (check_PNG_format(in) != 0)
	{
		fprintf(stderr, "It is not a PNG");
		fclose(in);
		return ERROR_UNSUPPORTED;
	}

	// reading IHDR
	struct Chunk ihdr = readChunk(in);
	if (ihdr.data == NULL)
	{
		fclose(in);
		fprintf(stderr, "Exception: not enough memory");
		return ERROR_OUT_OF_MEMORY;
	}
	if (strncmp(ihdr.name, "IHDR", 4) || ihdr.size != 13)
	{
		fclose(in);
		free(ihdr.data);
		fprintf(stderr, "Invalid file");
		return ERROR_OUT_OF_MEMORY;
	}
	int width = ihdr.data[0] * 256 * 256 * 256 + ihdr.data[1] * 256 * 256 + ihdr.data[2] * 256 + ihdr.data[3];
	int height = ihdr.data[4] * 256 * 256 * 256 + ihdr.data[5] * 256 * 256 + ihdr.data[6] * 256 + ihdr.data[7];
	int bitDepth = ihdr.data[8];
	int colorType = ihdr.data[9];
	int compression = ihdr.data[10];
	int filterMethod = ihdr.data[11];
	int interlace = ihdr.data[12];
	free(ihdr.data);
	if (!(colorType == 0 || colorType == 2 || colorType == 3) || compression || filterMethod || interlace ||
		bitDepth != 8 || width == 0 || height == 0)
	{
		fclose(in);
		fprintf(stderr, "Invalid file");
		return ERROR_DATA_INVALID;
	}
	int pixel = 1;
	if (colorType == 2 || colorType == 3)
	{
		pixel *= 3;
	}

	// reading PLTE
	struct Chunk plte;
	Color *palette = NULL;
	if (colorType == 3)
	{
		plte = read_plte_chunk(in);
		palette = malloc(plte.size / 3 * 3);
		for (int i = 0; i < plte.size / 3; ++i)
		{
			palette[i].red = plte.data[3 * i];
			palette[i].green = plte.data[3 * i + 1];
			palette[i].blue = plte.data[3 * i + 2];
		}
	}

	// reading IDAT and writing in image
	byte* image = malloc(width * height * pixel);
	if (image == NULL)
	{
		fclose(in);
		fprintf(stderr, "Exception: not enough memory");
		return ERROR_OUT_OF_MEMORY;
	}
	bool end = 0;
	size_t length = 0;
	struct Chunk idat;
	while (!feof(in))
	{
		idat = readChunk(in);
		if (idat.data == NULL)
		{
			fclose(in);
			free(image);
			fprintf(stderr, "Exception: not enough memory");
			return ERROR_OUT_OF_MEMORY;
		}
		if (!strncmp(idat.name, "IDAT", 4))
		{

			{
				for (size_t i = length; i < length + idat.size; i++)
				{
					image[i] = idat.data[i - length];
				}
			}
			length += idat.size;
		}
		if (!strncmp(idat.name, "PLTE", 4))
		{
			fclose(in);
			free(idat.data);
			free(image);
			fprintf(stderr, "Incorrect file structure");
			return ERROR_UNKNOWN;
		}
		if (!strncmp(idat.name, "IEND", 4))
		{
//			if (colorType == 3) {
//				for (size_t i = length; i < length + idat.size; i+=3)
//				{
//					image[i] = palette[i - length].red;
//					image[i+1] = palette[i - length].green;
//					image[i+2] = palette[i - length].blue;
//				}
//			}
			end = 1;
			if (idat.size != 0)
			{
				fclose(in);
				free(idat.data);
				free(image);
				fprintf(stderr, "Invalid file");
				return ERROR_DATA_INVALID;
			}
		}
	}
	fclose(in);
	free(idat.data);
	if (!end)
	{
		free(image);
		fprintf(stderr, "Invalid file");
		return ERROR_DATA_INVALID;
	}

	// write IDAT in image
	size_t sizePnm = width * height;
	if (colorType == 2)
	{
		sizePnm *= 3;
	}
	byte* imageOut = malloc(sizePnm * 2);
	if (imageOut == NULL)
	{
		free(image);
		fprintf(stderr, "Exception: not enough memory");
		return ERROR_OUT_OF_MEMORY;
	}
	size_t available = sizePnm * 2;
	int res;

	// Deflate
#ifdef LIBDEFLATE
	res = libdeflate_zlib_decompress(libdeflate_alloc_decompressor(), image, length, imageOut, available, &sizePnm);
//	int decompressImage(const unsigned char* image, size_t length, unsigned char** imageOut, size_t* sizePnm)
//	{
//		struct libdeflate_decompressor* decompressor = libdeflate_alloc_decompressor();
//		if (!decompressor) {
//			return -1;
//		}
//
//		size_t available = *sizePnm;
//		*imageOut = malloc(available);
//		if (!*imageOut) {
//			libdeflate_free_decompressor(decompressor);
//			return -1;
//		}
//
//		size_t actualSize = libdeflate_zlib_decompress(decompressor, image, length, *imageOut, available, sizePnm);
//		libdeflate_free_decompressor(decompressor);
//
//		if (actualSize == LIBDEFLATE_INSUFFICIENT_SPACE) {
//			free(*imageOut);
//			*imageOut = NULL;
//			return -1;
//		}
//
//		return 0;
//	}
#elif defined(ZLIB)
	res = uncompress(imageOut, &available, image, length);
#elif defined(ISAL)
	struct inflate_state state;
	isal_inflate_init(&state);
	state.next_in = image;
	state.avail_in = length;
	state.next_out = imageOut;
	state.avail_out = available;
	state.crc_flag = IGZIP_ZLIB;
	res = isal_inflate(&state);
#endif

	free(image);
	if (res)
	{
		free(imageOut);
		fprintf(stderr, "Invalid file");
		return ERROR_DATA_INVALID;
	}

	// Write data to an array of arrays
	byte** imageMap = malloc(height * (sizeof(byte*)));
	if (imageMap == NULL)
	{
		free(imageOut);
		fprintf(stderr, "Exception: not enough memory");
		return ERROR_OUT_OF_MEMORY;
	}
	for (int i = 0; i < height; i++)
	{
		imageMap[i] = malloc(pixel * width + 1);
		if (imageMap[i] == NULL)
		{
			free(imageOut);
			for (int j = 0; j < i; j++)
			{
				free(imageMap[j]);
			}
			free(imageMap);
			fprintf(stderr, "Exception: not enough memory");
			return ERROR_OUT_OF_MEMORY;
		}
		memcpy(imageMap[i], imageOut + (pixel * width + 1) * i, pixel * width + 1);
	}
	free(imageOut);

	FILE* out = fopen(argv[2], "wb");
	if (out == NULL)
	{
		fprintf(stderr, "Error: Could not open input file \"%s\n", argv[2]);
		return ERROR_CANNOT_OPEN_FILE;
	}
	fprintf(out, "P%i\n%i %i\n255\n", colorType == 2 || colorType == 3 ? 6 : 5, width, height);
	int check = filter(width, height, colorType, pixel, &palette, imageMap, out);
	fclose(out);

	for (int i = 0; i < height; i++)
	{
		free(imageMap[i]);
	}
	free(imageMap);

	free(palette);

	if (check)
		return ERROR_DATA_INVALID;
	return SUCCESS;
}