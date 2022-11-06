from .word import Word


class Post:

    def __init__(self):
        self.words = list()

    def add_word(self, sent_num, pos, x1, y1, x2, y2, page):
        self.words.append(Word(sent_num, pos, x1, y1, x2, y2, page))

    def get_term_frequency(self):
        return len(self.words)

    def get_words(self):
        return self.words
