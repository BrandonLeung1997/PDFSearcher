from .post import Post


class Index:
    def __init__(self):
        self.posts = dict()
        self.sent_num = 0

    def add_word(self, stemmed_word, sent_num, pos, x1, y1, x2, y2, page):
        if stemmed_word in self.posts.keys():
            self.posts.get(stemmed_word).add_word(sent_num, pos, x1, y1, x2, y2, page)
        else:
            self.posts[stemmed_word] = Post()
            self.posts.get(stemmed_word).add_word(sent_num, pos, x1, y1, x2, y2, page)

    def set_sent_num(self, sent_num):
        self.sent_num = sent_num

    def get_sent_num(self):
        return self.sent_num

    def get_post(self, stemmed_word):
        return self.posts.get(stemmed_word)
