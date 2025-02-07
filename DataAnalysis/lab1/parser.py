import csv
import time

import numpy as np
import requests
from lxml import html
import re
import pandas as pd
from sklearn.preprocessing import OneHotEncoder, MinMaxScaler
from sklearn.impute import SimpleImputer

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:85.0) Gecko/20100101 Firefox/85.0'
}

url = 'https://auto.drom.ru/skoda/superb/'


def parse_pages(base_url, num_pages):
    all_data = []
    page_urls = [f"{base_url}page{i}/" for i in range(1, num_pages + 1)]
    for page_url in page_urls:
        all_data.extend(parse(page_url))
        time.sleep(1.5)
    return all_data


def parse(page_url):
    response = requests.get(page_url, headers=headers)
    tree = html.fromstring(response.content)

    cars = tree.xpath('//div[@data-ftid="bulls-list_bull"]')

    transmission_mapping = {
        'АКПП': 'автомат',
        'автомат': 'автомат',
        'механика': 'механика'
    }

    data = []
    for car in cars:
        try:
            # parse title
            title = car.xpath('.//a[@data-ftid="bull_title"]/h3/text()')[0].strip()

            year_match = re.search(r'\b\d{4}\b', title)
            year = year_match.group(0) if year_match else 'Unknown'
            price = car.xpath('.//span[@data-ftid="bull_price"]/text()')[0].strip()
            price = price.replace('\xa0', '')

            # parse description
            description_items = car.xpath('.//div[@data-ftid="component_inline-bull-description"]/span/text()')
            description_items = [item.strip() for item in description_items if item.strip() != ',']

            engine_info = description_items[0].strip()
            fuel_type = description_items[1].strip()
            transmission = description_items[2].strip()
            drivetrain = description_items[3].strip()
            mileage = description_items[4].strip()

            transmission = transmission_mapping.get(transmission, transmission)

            engine_volume_match = re.search(r'(\d+\.\d+)\s*л', engine_info)
            engine_power_match = re.search(r'(\d+)\s*л\.с\.', engine_info)

            engine_volume = engine_volume_match.group(1) if engine_volume_match else 'Unknown'
            engine_power = engine_power_match.group(1) if engine_power_match else 'Unknown'

            mileage = re.sub(r'\s+', '', mileage.replace('км', '').strip())

            # parse evaluation
            evaluation_block = car.xpath('.//div[contains(@class, "css-1fuv9n1 evjskuu1")]/div/text()')
            evaluation = evaluation_block[0].strip() if evaluation_block else 'Unknown'
            if evaluation == "без оценки":
                evaluation = "Unknown"

            data.append({
                'year': year,
                'price': price,
                'engine_volume_l': engine_volume,
                'engine_power_hp': engine_power,
                'fuel_type': fuel_type,
                'transmission': transmission,
                'drivetrain': drivetrain,
                'mileage_km': mileage,
                'evaluation': evaluation
            })
        except IndexError:
            continue

    return data


def save_to_tsv(data, filename='cars_data.tsv'):
    with open(filename, 'w', newline='', encoding='utf-8') as file:
        writer = csv.DictWriter(file, fieldnames=data[0].keys(), delimiter='\t')
        writer.writeheader()
        writer.writerows(data)


def convert_to_arff(tsv_filename='cars_data.tsv', arff_filename='cars_data.arff'):
    with open(tsv_filename, 'r', encoding='utf-8') as tsv_file:
        reader = csv.reader(tsv_file, delimiter='\t')
        _ = next(reader)

        with open(arff_filename, 'w', encoding='utf-8') as arff_file:
            arff_file.write('@RELATION cars\n\n')

            arff_file.write('@ATTRIBUTE year NUMERIC\n')
            arff_file.write('@ATTRIBUTE price NUMERIC\n')
            arff_file.write('@ATTRIBUTE engine_volume_l NUMERIC\n')
            arff_file.write('@ATTRIBUTE engine_power_hp NUMERIC\n')
            arff_file.write('@ATTRIBUTE fuel_type STRING\n')
            arff_file.write('@ATTRIBUTE transmission STRING\n')
            arff_file.write('@ATTRIBUTE drivetrain STRING\n')
            arff_file.write('@ATTRIBUTE mileage_km NUMERIC\n')
            arff_file.write('@ATTRIBUTE evaluation STRING\n\n')

            arff_file.write('@DATA\n')
            for row in reader:
                row = ['?' if value == 'Unknown' else value for value in row]
                arff_file.write(','.join(row) + '\n')


def preprocess_data(tsv_filename='cars_data.tsv', preprocessed_filename='cars_data.csv'):
    data = pd.read_csv(tsv_filename, sep='\t')
    data.replace('Unknown', np.nan, inplace=True)

    imputer = SimpleImputer(strategy='most_frequent')
    data_filled = pd.DataFrame(imputer.fit_transform(data), columns=data.columns)

    categorical_features = ['fuel_type', 'transmission', 'drivetrain']
    encoder = OneHotEncoder(sparse_output=False, handle_unknown='ignore')
    encoded_features = encoder.fit_transform(data_filled[categorical_features])

    encoded_feature_names = encoder.get_feature_names_out(categorical_features)
    encoded_features = encoded_features.astype(int)

    encoded_df = pd.DataFrame(encoded_features, columns=encoded_feature_names)

    data_filled = data_filled.drop(columns=categorical_features)
    data_preprocessed = pd.concat([data_filled, encoded_df], axis=1)

    # Normalize
    numerical_features = ['year', 'price', 'engine_volume_l', 'engine_power_hp', 'mileage_km']
    scaler = MinMaxScaler()
    data_preprocessed[numerical_features] = scaler.fit_transform(data_preprocessed[numerical_features])

    data_preprocessed.to_csv(preprocessed_filename, index=False)


if __name__ == '__main__':
    num_pages = 29
    data = parse_pages(url, num_pages)
    save_to_tsv(data)
    convert_to_arff()
    preprocess_data()
