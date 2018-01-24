from flask import Flask, request, Response
from PIL import Image
from homo import findNotes
from flask import jsonify

# Initialize the Flask application
app = Flask(__name__)


# route http posts to this method
@app.route('/upload', methods=['POST'])
def get_img():
    img = Image.open(request.files['file'])
    result = findNotes(img)
    print result
    print sum(result)
    resp = {'status': 'Falso',
            'total' : str(sum(result))}
    return jsonify(resp)

@app.route('/test', methods=['GET'])
def test():
    return 'Online bois'
# start flask app
app.run(host="0.0.0.0", port=5000)
