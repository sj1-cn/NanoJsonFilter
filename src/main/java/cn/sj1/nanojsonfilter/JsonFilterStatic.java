package cn.sj1.nanojsonfilter;


import static cn.sj1.nanojsonfilter.NavType.ARRAY;
import static cn.sj1.nanojsonfilter.NavType.OBJECT;

public class JsonFilterStatic {
	static String filter(String jsonString) {
		char[] chars = jsonString.toCharArray();
		char[] targetChars = new char[chars.length];
		int length = chars.length;
		NavType navIn;
		char c;
		int stackIndex = 0;
		NavType[] stack = new NavType[100];

		int pos = 0;
		int targetIndex = 0;

		pos = skipSpace(chars, length, pos);

		if (chars[pos] == '{') {
			stack[stackIndex++] = OBJECT;
			navIn = OBJECT;
			targetChars[targetIndex++] = chars[pos++];
		} else if (chars[pos] == '[') {
			stack[stackIndex++] = ARRAY;
			navIn = ARRAY;
			targetChars[targetIndex++] = chars[pos++];
		} else {
			throw new RuntimeException("not start with { or [");
		}

		while (pos < length) {
			if (navIn == OBJECT) {
				while (pos < length) { // for properties
					pos = skipSpace(chars, length, pos);

					// name
					// , or }

					c = chars[pos];
					if (c == ',') {
						targetChars[targetIndex++] = chars[pos++];
						pos = skipSpace(chars, length, pos);
					} else if (c == '}') {
						targetChars[targetIndex++] = chars[pos++];

						--stackIndex;
						NavType result;
						if(stackIndex == 0) {
							result = NavType.EXIT;
						} else {
							result = stack[stackIndex - 1];
						}
						navIn = result;
						break;
					}


					int k = navString(chars, targetChars, pos, targetIndex, length);
					pos += k;
					targetIndex += k;

					pos = skipSpace(chars, length, pos);

					if (pos >= length) {
						throw new RuntimeException("name not end with :");
					}
					if (chars[pos] != ':') {
						throw new RuntimeException("name not end with :");
					}
					targetChars[targetIndex++] = ':';
					pos++;

					pos = skipSpace(chars, length, pos);

					// value
					c = chars[pos];
					if (c == '{') {
						stack[stackIndex++] = OBJECT;
						targetChars[targetIndex++] = chars[pos++];
						break;
					} else if (c == '[') {
						stack[stackIndex++] = ARRAY;
						navIn = ARRAY;
						targetChars[targetIndex++] = chars[pos++];
						break;
					} else {
						k = navValue(chars, targetChars, pos, targetIndex, length);
						pos += k;
						targetIndex += k;
					}

					pos = skipSpace(chars, length, pos);


				}

			} else if(navIn == ARRAY){
				while (pos < length) { // for properties
					// skip space and new line
					pos = skipSpace(chars, length, pos);
					// , or }
					c = chars[pos];
					if (c == ',') {
						targetChars[targetIndex++] = chars[pos++];
						pos = skipSpace(chars, length, pos);
					} else if (c == ']') {
						targetChars[targetIndex++] = chars[pos++];

						--stackIndex;
						NavType result;
						if(stackIndex == 0) {
							result = NavType.EXIT;
						} else {
							result = stack[stackIndex - 1];
						}
						navIn = result;
						break;
					}


					// name
					if (chars[pos] == '}') {
						targetChars[targetIndex++] = chars[pos++];
						--stackIndex;
						NavType result;
						if(stackIndex == 0) {
							result = NavType.EXIT;
						} else {
							result = stack[stackIndex - 1];
						}
						navIn = result;
						break;
					}

					// value
					c = chars[pos];
					if (c == '{') {
						targetChars[targetIndex++] = chars[pos++];
						stack[stackIndex++] = OBJECT;
						navIn = OBJECT;
						break;
					} else if (c == '[') {
						targetChars[targetIndex++] = chars[pos++];
						stack[stackIndex++] = ARRAY;
						break;
					} else {
						int k = navValue(chars, targetChars, pos, targetIndex, length);
						pos += k;
						targetIndex += k;
					}

					pos = skipSpace(chars, length, pos);


				}
			}else{
				break;
			}
		}

		return new String(targetChars, 0, targetIndex);
	}

	static int navString(char[] chars, char[] targetChars, int pos, int targetIndex, int length) {
		int k = 0;
		targetChars[targetIndex] = chars[pos];
		k++;

		for (; pos + k < length; k++) {
			char c = chars[pos + k];
			targetChars[targetIndex + k] = c;
			if (c == '\\' && chars[pos + k + 1] == '"') {
				k++;
				targetChars[targetIndex + k] = '"';
			} else if (c == '"') {
				k++;
				break;
			}
		}
		return k;
	}

	static void navTrue(char[] chars, char[] targetChars, int pos, int targetIndex, int length) {
		targetChars[targetIndex] = 't';
		if (pos + 3 < length && chars[pos + 1] == 'r' && chars[pos + 2] == 'u' && chars[pos + 3] == 'e') {
			targetChars[targetIndex + 1] = 'r';
			targetChars[targetIndex + 2] = 'u';
			targetChars[targetIndex + 3] = 'e';
		} else {
			throw new RuntimeException("not true");
		}
	}

	static int navNumber(char[] targetChars, int targetIndex, int pos, int length, char[] chars) {
		int k = 0;

		targetChars[targetIndex ] = chars[pos];
		k++;

		for (; pos + k < length; k++) {
			char c = chars[pos + k];
			if (('0' <= c && c <= '9') || c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+') {
				targetChars[targetIndex + k] = c;
			} else {
				break;
			}
		}
		return k;
	}

	static int skipSpace(char[] chars, int length, int i) {
		char c;
		for (; i < length; i++) {
			c = chars[i];
			if (c != ' ' && c != '\n' && c != '\r' && c != '\t') {
				break;
			}
		}
		return i;
	}

	static int navValue(char[] chars, char[] targetChars, int pos, int targetIndex, int length) {
		char c = chars[pos];
		int k;
		if (c == '"') { // string
			k = navString(chars, targetChars, pos, targetIndex, length);
		} else if (c == '-' || (c >= '0' && c <= '9')) { // number
			k = navNumber(targetChars, targetIndex, pos, length, chars);
		} else if (c == 't') { // true
			navTrue(chars, targetChars, pos, targetIndex, length);
			k = 4;
		} else if (c == 'f') { // false
			k = navFalse(chars, targetChars, pos, targetIndex, length);
		} else if (c == 'n') { // null
			k = navNull(chars, targetChars, pos, targetIndex, length);
		} else {
			throw new RuntimeException("not value " + c + " " + new String(targetChars, 0, targetIndex));
		}
		return k;
	}

	static private int navNull(char[] chars, char[] targetChars, int pos, int targetIndex, int length) {
		targetChars[targetIndex] = 'n';
		if (pos + 3 < length && chars[pos + 1] == 'u' && chars[pos + 2] == 'l' && chars[pos + 3] == 'l') {
			targetChars[targetIndex + 1] = 'u';
			targetChars[targetIndex + 2] = 'l';
			targetChars[targetIndex + 3] = 'l';
		} else {
			throw new RuntimeException("not null");
		}
		return 4;
	}

	static private int navFalse(char[] chars, char[] targetChars, int pos, int targetIndex, int length) {
		targetChars[targetIndex] = 'f';
		if (pos + 4 < length && chars[pos + 1] == 'a' && chars[pos + 2] == 'l' && chars[pos + 3] == 's' && chars[pos + 4] == 'e') {
			targetChars[targetIndex + 1] = 'a';
			targetChars[targetIndex + 2] = 'l';
			targetChars[targetIndex + 3] = 's';
			targetChars[targetIndex + 4] = 'e';
		} else {
			throw new RuntimeException("not false");
		}
		return 5;
	}
}
