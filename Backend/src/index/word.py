class Word:
    def __init__(self, sentence_num, part_of_speech, x1, y1, x2, y2, page):
        self.sentence_num = sentence_num
        self.part_of_speech = part_of_speech
        self.x1 = x1
        self.y1 = y1
        self.x2 = x2
        self.y2 = y2
        self.page = page

    def get_pos(self):
        return self.part_of_speech

    def get_sent_num(self):
        return self.sentence_num

    def get_x1(self):
        return self.x1

    def get_y1(self):
        return self.y1

    def get_x2(self):
        return self.x2

    def get_y2(self):
        return self.y2
